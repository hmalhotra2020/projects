package com.example.processor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.spark.sql.functions.hour;
import static org.apache.spark.sql.functions.sum;
import static org.apache.spark.sql.functions.to_date;
import static org.apache.spark.sql.functions.to_timestamp;

/**
 * Unified Spark Processor Application.
 * Starts a single Spark Streaming application that consumes from 'purchases' topic,
 * parses messages, aggregates them in-memory, and writes to both 'city_revenue'
 * and 'daily_revenue' PostgreSQL tables.
 */
public class SparkProcessorApp {

    public static void main(String[] args) throws InterruptedException {
        String kafkaBootstrap = System.getenv().getOrDefault("KAFKA_BOOTSTRAP", "localhost:9092");
        String postgresUrl    = System.getenv().getOrDefault("POSTGRES_URL",  "jdbc:postgresql://localhost:5433/pumpsdb");
        String postgresUser   = System.getenv().getOrDefault("POSTGRES_USER", "pguser");
        String postgresPass   = System.getenv().getOrDefault("POSTGRES_PASS", "pgpass");

        System.out.println("[SparkProcessorApp] Starting with Kafka: " + kafkaBootstrap);
        System.out.println("[SparkProcessorApp] Postgres URL: " + postgresUrl);

        SparkConf sparkConf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("UnifiedSparkProcessor")
                .set("spark.sql.legacy.timeParserPolicy", "LEGACY");

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "grp-unified");
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Collection<String> topics = List.of("purchases");

        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));
        SQLContext sqlContext = new SQLContext(jssc.sparkContext());

        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams));

        StructType schema = new StructType(new StructField[]{
                new StructField("petrolPumpId", DataTypes.StringType,  true, Metadata.empty()),
                new StructField("machineId",    DataTypes.StringType,  true, Metadata.empty()),
                new StructField("city",         DataTypes.StringType,  true, Metadata.empty()),
                new StructField("purchaseTime", DataTypes.StringType,  true, Metadata.empty()),
                new StructField("fuelType",     DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("qnty",         DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("amt",          DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("pType",        DataTypes.IntegerType, true, Metadata.empty())
        });

        JavaDStream<String> lineStream = stream.map(ConsumerRecord::value);
        JavaDStream<Row> rowStream = lineStream
                .filter(s -> s != null && s.split("\\|").length >= 8)
                .map(s -> {
                    String[] f = s.split("\\|");
                    return RowFactory.create(f[0], f[1], f[2], f[3],
                            Integer.parseInt(f[4].trim()), Integer.parseInt(f[5].trim()),
                            Integer.parseInt(f[6].trim()), Integer.parseInt(f[7].trim()));
                });

        rowStream.foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {
                Dataset<Row> df = sqlContext.createDataFrame(rdd, schema);
                Dataset<Row> withTs = df.withColumn("purchaseTs", to_timestamp(df.col("purchaseTime")));

                // 1. City Revenue Aggregation
                Dataset<Row> cityDf = withTs
                        .groupBy(to_date(withTs.col("purchaseTs")).alias("pdate"), withTs.col("city"))
                        .agg(sum("amt").alias("total_amt"));
                System.out.println("--- City Revenue Aggregation ---");
                cityDf.show();

                // 2. Daily Revenue Aggregation
                Dataset<Row> dailyDf = withTs
                        .groupBy(
                                to_date(withTs.col("purchaseTs")).alias("pdate"),
                                hour(withTs.col("purchaseTs")).alias("hour"),
                                withTs.col("fuelType"))
                        .agg(sum("qnty").alias("qty"), sum("amt").alias("amt"));
                System.out.println("--- Daily Revenue Aggregation ---");
                dailyDf.show();

                // Write to PostgreSQL
                Properties props = new Properties();
                props.setProperty("user", postgresUser);
                props.setProperty("password", postgresPass);
                props.setProperty("driver", "org.postgresql.Driver");

                cityDf.write().mode(SaveMode.Append).jdbc(postgresUrl, "city_revenue", props);
                dailyDf.write().mode(SaveMode.Append).jdbc(postgresUrl, "daily_revenue", props);
            }
        });

        try {
            jssc.start();
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            jssc.close();
        }
    }
}
