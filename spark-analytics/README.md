# Petrol Pumps Real-Time Analytics System

This directory consolidates and containerizes the real-time petrol pump purchase analytics system, consisting of two main applications communicating over Kafka and saving results to PostgreSQL.

## 🏗️ Architecture

```
                                  +-----------------------+
                                  |   PurchaseSimulator   | (Generates random pump purchases
                                  |     (Spring Boot)     |  every 5 seconds)
                                  +-----------+-----------+
                                              |
                                              | (Topic: "purchases")
                                              v
                                     +-----------------+
                                     |  Apache Kafka   |
                                     +---+---------+---+
                                         |         |
                  +----------------------+         +----------------------+
                  |                                                       |
                  v (Consumer Group: "grp-unified")                       v (Consumer Group: "pumps-analyzer")
      +------------------------+                              +------------------------+
      | UnifiedSparkProcessor  |                              |    PurchaseAnalyzer    |
      |   (Apache Spark 3.5)   |                              |     (Spring Boot)      |
      +-----------+------------+                              +-----------+------------+
                  |                                                       |
                  +----------------------+         +----------------------+
                                         |         |
                                         v         v
                                    +-----------------+
                                    |   PostgreSQL    | (Stores aggregated tables:
                                    |    (pumpsdb)    |  city_revenue & daily_revenue)
                                    +--------+--------+
                                             ^
                                             |
                                    +--------+--------+
                                    |  REST API & UI  | (Serves Chart.js web dashboard
                                    |     (Port 8080) |  at http://localhost:8080)
                                    +-----------------+
```

---

## 📂 Directories

- **[`/pumps-analyzer-app`](file:///Users/rajm/work_all/ai/pumps-analyzer-app)**: Spring Boot 3 app containing the Purchase Simulator, a lightweight aggregator (redundancy listener), and the web dashboard interface.
- **[`/kafka-spark-processor`](file:///Users/rajm/work_all/ai/kafka-spark-processor)**: Spark Streaming app in Java/Scala that consumes the topic and writes hourly/daily aggregations to the DB.

---

## ⚡ Quick Start

1. **Prerequisite**: Make sure Docker Desktop is open and running on your machine.
2. **Start All Services**:
   Run the following command from the root directory to spin up the Kafka broker, Zookeeper, PostgreSQL database, and both JVM applications:
   ```bash
   docker compose up -d
   ```
3. **Open the Dashboard**:
   Go to: **[http://localhost:8080](http://localhost:8080)**

---

## 🛠️ Docker Cheat Sheet

### 1. Running the Services

| Action | Command | Notes |
| :--- | :--- | :--- |
| **Start All (Background)** | `docker compose up -d` | Starts all services in the background. |
| **Build & Start (Foreground)** | `docker compose up --build` | Re-compiles code and streams all logs to the terminal. |
| **Start Specific Service** | `docker compose up -d spark-processor` | Re-runs or starts just one service. |
| **Rebuild Specific Service** | `docker compose build pumps-analyzer-app` | Forces a rebuild of the JAR & image for that app. |

### 2. Stopping the Services

| Action | Command | Notes |
| :--- | :--- | :--- |
| **Stop Services** | `docker compose stop` | Stops container execution without destroying data. |
| **Shutdown & Clean Network** | `docker compose down` | Stops and removes container instances and networks. |
| **Wipe All Data & Volumes** | `docker compose down -v` | Deletes all containers, networks, and the PostgreSQL database volume (resets DB to empty). |

### 3. Debugging & Logs

| Action | Command | Notes |
| :--- | :--- | :--- |
| **Check Logs (Live Stream)** | `docker compose logs -f` | Follows logs from all containers in real time. |
| **Check App Logs** | `docker compose logs -f pumps-analyzer-app` | Monitors Spring Boot (Simulator / REST API / Dashboard). |
| **Check Spark Logs** | `docker compose logs -f spark-processor` | Monitors Spark Streaming aggregation jobs. |
| **Check Service Statuses** | `docker compose ps` | Lists all running containers, status, and mapped ports. |

---

## 💾 Interacting with the PostgreSQL Database

If you want to query the Postgres DB inside Docker directly from your terminal:

* **Open the SQL Shell (`psql`)**:
  ```bash
  docker exec -it shared-postgres psql -U pguser -d pumpsdb
  ```
* **Quick Query (City Aggregates)**:
  ```bash
  docker exec -i shared-postgres psql -U pguser -d pumpsdb -c "SELECT * FROM city_revenue ORDER BY total_amt DESC LIMIT 10;"
  ```
* **Quick Query (Daily Aggregates)**:
  ```bash
  docker exec -i shared-postgres psql -U pguser -d pumpsdb -c "SELECT * FROM daily_revenue ORDER BY qty DESC LIMIT 10;"
  ```
