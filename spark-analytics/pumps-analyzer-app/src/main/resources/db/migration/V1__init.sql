-- V1__init.sql
-- Initial schema for the Pumps Analyzer Application.
-- Creates city_revenue and daily_revenue tables with indexes.

CREATE TABLE IF NOT EXISTS city_revenue (
    id         BIGSERIAL PRIMARY KEY,
    pdate      DATE         NOT NULL,
    city       VARCHAR(100) NOT NULL,
    total_amt  BIGINT       DEFAULT 0,
    created_at TIMESTAMP    DEFAULT NOW(),
    updated_at TIMESTAMP    DEFAULT NOW(),
    CONSTRAINT uq_city_revenue UNIQUE (pdate, city)
);

CREATE TABLE IF NOT EXISTS daily_revenue (
    id         BIGSERIAL PRIMARY KEY,
    pdate      DATE      NOT NULL,
    hour       INT       NOT NULL,
    fuel_type  INT       NOT NULL,
    qty        BIGINT    DEFAULT 0,
    amt        BIGINT    DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uq_daily_revenue UNIQUE (pdate, hour, fuel_type)
);

CREATE INDEX IF NOT EXISTS idx_city_rev_city ON city_revenue (city);
CREATE INDEX IF NOT EXISTS idx_city_rev_amt  ON city_revenue (total_amt DESC);
CREATE INDEX IF NOT EXISTS idx_daily_rev_date ON daily_revenue (pdate);
