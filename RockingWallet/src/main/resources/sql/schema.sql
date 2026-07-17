CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    state VARCHAR(20) NOT NULL,
    balance NUMERIC(18,2) NOT NULL,
    minimum_balance NUMERIC(18,2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS intents (
    intent_id BIGINT PRIMARY KEY,
    from_account_id BIGINT NOT NULL,
    to_account_id BIGINT NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    txn_type VARCHAR(20) NOT NULL,
    state VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS ledger_entries (
    ledger_id BIGSERIAL PRIMARY KEY,
    intent_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    entry_type VARCHAR(10) NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

create table customers (
    customer_id bigint primary key,
    name varchar(100),
    email varchar(100),
    phone varchar(20),
    status varchar(20),
    created_at timestamp,
    updated_at timestamp
);

create table transactions (
    transaction_id bigserial primary key,
    intent_id bigint,
    txn_type varchar(20),
    from_account_id bigint,
    to_account_id bigint,
    amount numeric,
    created_at timestamp
);

insert into customers
(customer_id, name, email, phone, status, created_at, updated_at)
values(0, 'SYSTEM', 'system@wallet.local', '0000000000', 'ACTIVE', now(), now());

insert into accounts
(account_id, customer_id, state, balance, minimum_balance)
values (0, 0, 'ACTIVE', 1000000000, -1000000000);
