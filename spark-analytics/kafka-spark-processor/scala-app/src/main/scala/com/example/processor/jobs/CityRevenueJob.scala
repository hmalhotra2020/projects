package com.example.processor.jobs

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SQLContext, SaveMode}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import java.util.Properties

/**
 * Scala reference implementation of CityRevenueJob.
 * Aggregates purchase amounts by city and date.
 * Reads Kafka topic 'purchases', writes to Postgres table 'city_revenue'.
 */
class CityRevenueJob(
  kafkaBootstrap: String,
  postgresUrl: String,
  postgresUser: String,
  postgresPass: String
) extends Serializable {

  def evalStream(): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("CityRevenueJob-Scala")
      .set("spark.sql.legacy.timeParserPolicy", "LEGACY")
      .set("spark.driver.allowMultipleContexts", "true")

    val kafkaParams: Map[String, Object] = Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG        -> kafkaBootstrap,
      ConsumerConfig.GROUP_ID_CONFIG                 -> "grp-city-scala",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG   -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG        -> "earliest"
    )

    val topics = Set("purchases")

    val ssc       = new StreamingContext(sparkConf, Durations.seconds(5))
    val sqlContext = new SQLContext(ssc.sparkContext)

    val schema = StructType(Seq(
      StructField("petrolPumpId", StringType,  nullable = true),
      StructField("machineId",    StringType,  nullable = true),
      StructField("city",         StringType,  nullable = true),
      StructField("purchaseTime", StringType,  nullable = true),
      StructField("fuelType",     IntegerType, nullable = true),
      StructField("qnty",         IntegerType, nullable = true),
      StructField("amt",          IntegerType, nullable = true),
      StructField("pType",        IntegerType, nullable = true)
    ))

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    val rowStream = stream
      .map(_.value())
      .filter(s => s != null && s.split("\\|").length >= 8)
      .map { s =>
        val f = s.split("\\|")
        Row(f(0), f(1), f(2), f(3),
          f(4).trim.toInt, f(5).trim.toInt,
          f(6).trim.toInt, f(7).trim.toInt)
      }

    val pgUrl  = postgresUrl
    val pgUser = postgresUser
    val pgPass = postgresPass

    rowStream.foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
        val df = sqlContext.createDataFrame(rdd, schema)
        val aggregated = df
          .groupBy(
            to_date(df("purchaseTime")).alias("pdate"),
            df("city")
          )
          .agg(sum("amt").alias("total_amt"))
        aggregated.show()
        val props = new Properties()
        props.setProperty("user",     pgUser)
        props.setProperty("password", pgPass)
        props.setProperty("driver",   "org.postgresql.Driver")
        aggregated.write.mode(SaveMode.Append).jdbc(pgUrl, "city_revenue", props)
      }
    }

    ssc.start()
    try ssc.awaitTermination()
    finally ssc.stop(stopSparkContext = true, stopGracefully = true)
  }
}
