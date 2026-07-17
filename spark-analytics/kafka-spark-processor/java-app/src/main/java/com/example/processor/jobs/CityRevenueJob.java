package com.example.processor.jobs;

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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.spark.sql.functions.sum;
import static org.apache.spark.sql.functions.to_date;

/**
 * Spark Streaming job that aggregates purchase amounts by city per day.
 * Reads from Kafka topic 'purchases', writes to Postgres table 'city_revenue'.
 * Mirrors the original PPEvaluatorJob4 (MySQL -> Postgres, Spark 2.4 -> 3.5).
 */
public class CityRevenueJob implements Serializable {

    private final String kafkaBootstrap;
    private final String postgresUrl;
    private final String postgresUser;
    private final String postgresPass;

    public CityRevenueJob(String kafkaBootstrap, String postgresUrl, String postgresUser, String postgresPass) {
        this.kafkaBootstrap = kafkaBootstrap;
        this.postgresUrl = postgresUrl;
        this.postgresUser = postgresUser;
        this.postgresPass = postgresPass;
    }

    public void evalStream() {
        SparkConf sparkConf = new SparkConf()
                .setMaster("local[2]")
                .setAppName("CityRevenueJob")
                .set("spark.sql.legacy.timeParserPolicy", "LEGACY")
                .set("spark.driver.allowMultipleContexts", "true");

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrap);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "grp-city");
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

        String pgUrl  = postgresUrl;
        String pgUser = postgresUser;
        String pgPass = postgresPass;

        rowStream.foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {
                Dataset<Row> df = sqlContext.createDataFrame(rdd, schema);
                Dataset<Row> aggregated = df
                        .groupBy(
                                to_date(df.col("purchaseTime")).alias("pdate"),
                                df.col("city"))
                        .agg(sum("amt").alias("total_amt"));
                aggregated.show();
                Properties props = new Properties();
                props.setProperty("user", pgUser);
                props.setProperty("password", pgPass);
                props.setProperty("driver", "org.postgresql.Driver");
                aggregated.write().mode(SaveMode.Append).jdbc(pgUrl, "city_revenue", props);
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
