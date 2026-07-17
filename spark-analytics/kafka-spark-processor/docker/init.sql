CREATE TABLE IF NOT EXISTS city_revenue (
    id SERIAL PRIMARY KEY,
    pdate DATE NOT NULL,
    city VARCHAR(100) NOT NULL,
    total_amt BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS daily_revenue (
    id SERIAL PRIMARY KEY,
    pdate DATE NOT NULL,
    hour INT NOT NULL,
    fuel_type INT NOT NULL,
    qty BIGINT DEFAULT 0,
    amt BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_city_revenue_pdate ON city_revenue(pdate);
CREATE INDEX IF NOT EXISTS idx_daily_revenue_pdate ON daily_revenue(pdate);
