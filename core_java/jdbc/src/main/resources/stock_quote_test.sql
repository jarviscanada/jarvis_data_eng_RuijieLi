DROP DATABASE IF EXISTS stock_quote_test;
CREATE DATABASE stock_quote_test;
\c stock_quote_test;

DROP TABLE IF EXISTS quote;
CREATE TABLE quote(
    symbol              VARCHAR(10) PRIMARY KEY,
    open                DECIMAL(10, 2) NOT NULL,
    high                DECIMAL(10, 2) NOT NULL,
    low                 DECIMAL(10, 2) NOT NULL,
    price               DECIMAL(10, 2) NOT NULL,
    volume              INT NOT NULL,
    latest_trading_day  DATE NOT NULL,
    previous_close      DECIMAL(10, 2) NOT NULL,
    change              DECIMAL(10, 2) NOT NULL,
    change_percent      VARCHAR(10) NOT NULL,
    "timestamp"         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP::TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
DROP TABLE IF EXISTS position;
CREATE TABLE position (
    symbol                VARCHAR(10) PRIMARY KEY,
    number_of_shares      INT NOT NULL,
    value_paid            DECIMAL(10, 2) NOT NULL,
    CONSTRAINT symbol_fk	FOREIGN KEY (symbol) REFERENCES quote(symbol)
);