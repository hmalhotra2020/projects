# kafka-spark-processor

A modernised **Spark Streaming + Kafka** petrol-pump purchase-order aggregation pipeline,
upgraded from Spark 2.4 / MySQL to **Spark 3.5 / Kafka 3.6 / PostgreSQL 16**.

The project is a multi-module Gradle build with:
- `java-app/`  – production Java 17 implementation
- `scala-app/` – Scala 2.13 reference implementation (feature-equivalent)

---

## Architecture

```
  Kafka Topic: purchases
         │
         ▼
  ┌─────────────────────────────────────────────────────┐
  │           Spark Streaming (5-second micro-batches)  │
  │                                                     │
  │  ┌─────────────────┐   ┌───────────────────────┐   │
  │  │  CityRevenueJob │   │   DailyRevenueJob     │   │
  │  │  (grp-city)     │   │   (grp-daily)         │   │
  │  │                 │   │                       │   │
  │  │  GROUP BY       │   │  GROUP BY             │   │
  │  │  pdate, city    │   │  pdate, hour,         │   │
  │  │  SUM(amt)       │   │  fuelType             │   │
  │  │                 │   │  SUM(qnty), SUM(amt)  │   │
  │  └────────┬────────┘   └───────────┬───────────┘   │
  └───────────┼───────────────────────┼───────────────┘
              ▼                       ▼
        city_revenue           daily_revenue
        (PostgreSQL)           (PostgreSQL)
```

---

## Tech Stack

| Component          | Version         |
|--------------------|-----------------|
| Java               | 17              |
| Scala              | 2.13.12         |
| Spark              | 3.5.1           |
| Kafka Clients      | 3.6.1           |
| Spark-Kafka        | spark-streaming-kafka-0-10_2.13 |
| PostgreSQL Driver  | 42.7.2          |
| Confluent Platform | 7.5.0 (Docker)  |
| PostgreSQL Image   | 16-alpine        |
| Gradle Shadow Jar  | 8.1.1           |
| Gradle             | 8.5             |

---

## Quick Start — Docker Compose

### 1. Build the fat jar and start all services

```bash
cd /Users/rajm/work_all/ai/kafka-spark-processor
docker compose up --build
```

This will:
1. Build the Java fat jar inside a `gradle:8.5-jdk17` container
2. Start Zookeeper, Kafka, PostgreSQL, and the Spark processor

### 2. Produce test messages

Open a new terminal and exec into the Kafka container:

```bash
docker exec -it ksp-kafka bash

# Inside container:
kafka-console-producer \
  --broker-list localhost:29092 \
  --topic purchases

# Paste pipe-delimited records (one per line):
PUMP01|MACH01|Mumbai|2024-01-15 10:30:00|1|50|4500|1
PUMP02|MACH02|Delhi|2024-01-15 11:00:00|2|30|2700|2
PUMP01|MACH03|Mumbai|2024-01-15 12:00:00|1|70|6300|1
```

### 3. Check results in PostgreSQL

```bash
docker exec -it ksp-postgres psql -U pguser -d pumpsdb

-- City revenue aggregation
SELECT * FROM city_revenue ORDER BY created_at DESC;

-- Daily revenue by hour and fuel type
SELECT * FROM daily_revenue ORDER BY created_at DESC;
```

---

## Running java-app Standalone (without Docker)

**Prerequisites:** Java 17, Kafka running on `localhost:9092`, PostgreSQL on `localhost:5433`

```bash
cd /Users/rajm/work_all/ai/kafka-spark-processor

# Build the shadow jar
./gradlew :java-app:shadowJar

# Run
java \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  --add-opens=java.base/java.util=ALL-UNNAMED \
  --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
  -jar java-app/build/libs/java-app-all.jar
```

Override defaults via environment variables (see table below).

---

## Running scala-app Standalone

```bash
cd /Users/rajm/work_all/ai/kafka-spark-processor

# Build the shadow jar
./gradlew :scala-app:shadowJar

# Run
java \
  --add-opens=java.base/java.lang=ALL-UNNAMED \
  --add-opens=java.base/java.util=ALL-UNNAMED \
  --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
  -jar scala-app/build/libs/scala-app-all.jar
```

---

## PostgreSQL Tables

```sql
-- Aggregated city revenue per day
CREATE TABLE city_revenue (
    id         SERIAL PRIMARY KEY,
    pdate      DATE         NOT NULL,
    city       VARCHAR(100) NOT NULL,
    total_amt  BIGINT       DEFAULT 0,
    created_at TIMESTAMP    DEFAULT NOW()
);

-- Aggregated revenue by day + hour + fuel type
CREATE TABLE daily_revenue (
    id         SERIAL PRIMARY KEY,
    pdate      DATE      NOT NULL,
    hour       INT       NOT NULL,
    fuel_type  INT       NOT NULL,
    qty        BIGINT    DEFAULT 0,
    amt        BIGINT    DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

## Kafka Message Format

Pipe-delimited, 8 fields:

```
petrolPumpId|machineId|city|purchaseTime|fuelType|qnty|amt|pType
```

| Field        | Type   | Example              | Notes                           |
|--------------|--------|----------------------|---------------------------------|
| petrolPumpId | String | PUMP01               | Unique pump identifier          |
| machineId    | String | MACH01               | Machine/dispenser ID            |
| city         | String | Mumbai               | City name                       |
| purchaseTime | String | 2024-01-15 10:30:00  | Timestamp (yyyy-MM-dd HH:mm:ss) |
| fuelType     | Int    | 1                    | 1=Petrol, 2=Diesel, etc.        |
| qnty         | Int    | 50                   | Quantity in litres              |
| amt          | Int    | 4500                 | Amount in rupees                |
| pType        | Int    | 1                    | Payment type                    |

---

## Environment Variables

| Variable          | Default                                    | Description              |
|-------------------|--------------------------------------------|--------------------------|
| `KAFKA_BOOTSTRAP` | `localhost:9092`                           | Kafka bootstrap servers  |
| `POSTGRES_URL`    | `jdbc:postgresql://localhost:5433/pumpsdb` | JDBC URL for PostgreSQL  |
| `POSTGRES_USER`   | `pguser`                                   | PostgreSQL username      |
| `POSTGRES_PASS`   | `pgpass`                                   | PostgreSQL password      |

---

## Java vs Scala Implementation Notes

| Aspect               | Java (`java-app`)                     | Scala (`scala-app`)                   |
|----------------------|---------------------------------------|---------------------------------------|
| Language version     | Java 17 (records, var, streams)       | Scala 2.13 (case classes, implicits)  |
| Model                | `record PurchaseOrder(...)`           | `case class PurchaseOrder(...)`       |
| Kafka consumer group | `grp-city` / `grp-daily`             | `grp-city-scala` / `grp-daily-scala` |
| DataFrame API        | `Dataset<Row>` + `RowFactory`         | `DataFrame` + `Row`                   |
| Spark context stop   | `jssc.close()` in `finally`          | `ssc.stop(true, true)` in `finally`  |
| Null safety          | Explicit `.filter(s -> s != null)`    | Same via predicate lambda             |
| Build output         | `java-app/build/libs/*-all.jar`      | `scala-app/build/libs/*-all.jar`     |

Both implementations are feature-equivalent and produce identical output rows.
The Scala version demonstrates idiomatic Scala patterns for the same pipeline.

---

## Project Structure

```
kafka-spark-processor/
├── build.gradle                          # Root Gradle config (Java toolchain)
├── settings.gradle                       # Multi-module config
├── Dockerfile                            # Multi-stage build for java-app
├── docker-compose.yml                    # Full stack: ZK + Kafka + Postgres + App
├── docker/
│   └── init.sql                          # Postgres schema init
├── java-app/
│   ├── build.gradle                      # Shadow JAR + Spark + PG deps
│   └── src/main/java/com/example/processor/
│       ├── SparkProcessorApp.java        # Main entry point
│       ├── model/
│       │   └── PurchaseOrder.java        # Record model + pipe parser
│       └── jobs/
│           ├── CityRevenueJob.java       # City revenue aggregation
│           └── DailyRevenueJob.java      # Hourly/fuel-type aggregation
└── scala-app/
    ├── build.gradle                      # Scala + Shadow JAR deps
    └── src/main/scala/com/example/processor/
        ├── SparkProcessorApp.scala       # Scala entry point
        ├── model/
        │   └── PurchaseOrder.scala       # Case class + companion
        └── jobs/
            ├── CityRevenueJob.scala      # Scala city aggregation
            └── DailyRevenueJob.scala     # Scala daily aggregation
```
