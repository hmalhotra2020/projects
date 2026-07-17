package com.example.processor

import com.example.processor.jobs.{CityRevenueJob, DailyRevenueJob}

/**
 * Entry point for the Scala Spark Processor.
 * Equivalent to the Java implementation - serves as a learning reference.
 */
object SparkProcessorApp extends App {

  val kafkaBootstrap = sys.env.getOrElse("KAFKA_BOOTSTRAP", "localhost:9092")
  val postgresUrl    = sys.env.getOrElse("POSTGRES_URL",    "jdbc:postgresql://localhost:5433/pumpsdb")
  val postgresUser   = sys.env.getOrElse("POSTGRES_USER",   "pguser")
  val postgresPass   = sys.env.getOrElse("POSTGRES_PASS",   "pgpass")

  println(s"[SparkProcessorApp-Scala] Starting with Kafka: $kafkaBootstrap")
  println(s"[SparkProcessorApp-Scala] Postgres URL: $postgresUrl")

  val t1 = new Thread(
    () => new CityRevenueJob(kafkaBootstrap, postgresUrl, postgresUser, postgresPass).evalStream(),
    "CityRevenueJob"
  )
  val t2 = new Thread(
    () => new DailyRevenueJob(kafkaBootstrap, postgresUrl, postgresUser, postgresPass).evalStream(),
    "DailyRevenueJob"
  )

  t1.start()
  Thread.sleep(2000)
  t2.start()

  t1.join()
  t2.join()
  println("[SparkProcessorApp-Scala] All jobs finished.")
}
