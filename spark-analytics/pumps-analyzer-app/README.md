# pumps-analyzer-app

A **live petrol pump analytics dashboard** powered by Spring Boot 3.2, Apache Kafka, PostgreSQL, and Chart.js.

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                          pumps-analyzer-app                          │
│                                                                      │
│  ┌─────────────────────┐        ┌───────────────────────────────┐   │
│  │  PurchaseSimulator  │        │      PurchaseAnalyzer         │   │
│  │  (@Scheduled 5s)    │        │      (@KafkaListener)         │   │
│  │                     │        │                               │   │
│  │  Generates random   │──────▶ │  Parses pipe-string message   │   │
│  │  PurchaseOrders     │ Kafka  │  Upserts city_revenue &       │   │
│  │  for N pumps &      │ topic: │  daily_revenue in Postgres    │   │
│  │  M cities           │ purchases                             │   │
│  └─────────────────────┘        └───────────────────────────────┘   │
│                                              │                       │
│                                              ▼                       │
│                                   ┌──────────────────┐              │
│                                   │    PostgreSQL     │              │
│                                   │  city_revenue     │              │
│                                   │  daily_revenue    │              │
│                                   └──────────┬───────┘              │
│                                              │                       │
│                                              ▼                       │
│                              ┌───────────────────────────┐          │
│                              │     ChartsApiController    │          │
│                              │  GET /api/city-revenue     │          │
│                              │  GET /api/daily-revenue    │          │
│                              └────────────┬──────────────┘          │
│                                           │                          │
│                                           ▼                          │
│                              ┌───────────────────────────┐          │
│                              │   dashboard.html (Chart.js)│          │
│                              │   Polls every 5 seconds    │          │
│                              │   Doughnut | Bar | Line    │          │
│                              └───────────────────────────┘          │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Quick Start

### Prerequisites

- Docker & Docker Compose installed
- Ports **2181**, **9092**, **5432**, **8080** free on localhost

### Run everything with Docker Compose

```bash
cd pumps-analyzer-app
docker compose up --build
```

Open the dashboard: **http://localhost:8080**

> ℹ️ The first build downloads Gradle dependencies and may take 2–4 minutes.  
> Subsequent builds use the Docker layer cache and are much faster.

### Tear down

```bash
docker compose down -v   # -v also removes the Postgres volume
```

---

## Local Development (without Docker)

1. **Start only infrastructure** (Kafka + Postgres):
   ```bash
   docker compose up -d zookeeper kafka postgres
   ```

2. **Run the app**:
   ```bash
   ./gradlew bootRun
   ```

3. Open **http://localhost:8080**

---

## Features

- ⚡ **Live producer** — `PurchaseSimulator` generates random purchase orders every 5 s for configurable N pumps across 20 Indian cities
- 📨 **Kafka integration** — orders published as pipe-delimited strings to the `purchases` topic
- 🔄 **Kafka consumer** — `PurchaseAnalyzer` consumes, parses, and upserts aggregated revenue into Postgres
- 🗄️ **PostgreSQL persistence** — Flyway-managed schema (`city_revenue`, `daily_revenue`)
- 📊 **Live Chart.js dashboard** — glassmorphism dark-mode UI with:
  - 🍩 Doughnut: top-10 city revenue share
  - 🏆 Horizontal bar: revenue leaderboard
  - 📈 Dual-axis area line: daily qty + revenue trend
- 🔁 **Auto-refresh** — dashboard polls APIs every 5 s with smooth chart animations
- 🐳 **Docker-ready** — one `docker compose up --build` to run everything

---

## Configuration

All values can be overridden via environment variables (for Docker) or `application.yml`.

| Property | Env Variable | Default | Description |
|---|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/pumpsdb` | Postgres JDBC URL |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `pguser` | Postgres username |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | `pgpass` | Postgres password |
| `spring.kafka.bootstrap-servers` | `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker |
| `simulator.no-pumps` | — | `10` | Number of pump stations |
| `simulator.hits-per-tick` | — | `10` | Orders generated per tick |
| `simulator.tick-time` | — | `5000` | Tick interval in ms |
| `city.names` | — | (20 cities) | Comma-separated city list |

---

## API Endpoints

### `GET /api/city-revenue`

Returns top-10 cities by cumulative revenue (descending).

**Response:**
```json
[
  { "city": "Mumbai",    "totalAmt": 1250000 },
  { "city": "Delhi",     "totalAmt":  980000 },
  ...
]
```

### `GET /api/daily-revenue`

Returns daily revenue (qty + amt) aggregated by date, chronologically.

**Response:**
```json
{
  "dates": ["2024-01-14", "2024-01-15"],
  "qty":   [3500, 4200],
  "amt":   [385000, 462000]
}
```

---

## Data Flow

```
PetrolPump machines
      │
      ▼
PurchaseSimulator ──► [Kafka topic: purchases] ──► PurchaseAnalyzer
                                                          │
                                           ┌──────────────┴────────────┐
                                           ▼                           ▼
                                    city_revenue               daily_revenue
                                  (pdate, city,             (pdate, hour,
                                   total_amt)               fuel_type, qty, amt)
                                           │                           │
                                           └──────────────┬────────────┘
                                                          ▼
                                                 ChartsApiController
                                                  /api/city-revenue
                                                  /api/daily-revenue
                                                          │
                                                          ▼
                                                  dashboard.html
                                               (Chart.js, polls 5s)
```

---

## Project Structure

```
pumps-analyzer-app/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
├── README.md
└── src/main/
    ├── java/com/example/pumps/
    │   ├── PumpsAnalyzerApp.java          # @SpringBootApplication entry point
    │   ├── model/
    │   │   └── PurchaseOrder.java         # Pipe-delimited DTO
    │   ├── producer/
    │   │   ├── PetrolPump.java            # POJO: pump + machines
    │   │   ├── PPInfoProvider.java        # Creates N pump instances
    │   │   ├── CityProvider.java          # Reads city.names property
    │   │   └── PurchaseSimulator.java     # @Scheduled Kafka producer
    │   ├── consumer/
    │   │   └── PurchaseAnalyzer.java      # @KafkaListener → upserts
    │   ├── entity/
    │   │   ├── CityRevenue.java           # JPA entity: city_revenue
    │   │   └── DailyRevenue.java          # JPA entity: daily_revenue
    │   ├── store/
    │   │   ├── CityRevenueRepository.java
    │   │   └── DailyRevenueRepository.java
    │   ├── api/
    │   │   └── ChartsApiController.java   # REST: /api/city-revenue, /api/daily-revenue
    │   └── web/
    │       └── DashboardController.java   # MVC: GET /
    └── resources/
        ├── application.yml
        ├── db/migration/
        │   └── V1__init.sql               # Flyway schema
        └── templates/
            └── dashboard.html             # Chart.js glassmorphism dashboard
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.2 |
| Messaging | Apache Kafka (Confluent 7.5) |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Templating | Thymeleaf |
| Charts | Chart.js 4.x |
| Build | Gradle 8.5 |
| Runtime | Java 17 |
| Container | Docker + Docker Compose |
