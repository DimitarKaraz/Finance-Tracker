
DROP TABLE IF EXISTS exchange_rates;
CREATE TABLE exchange_rates (
    id INT PRIMARY KEY,
    currency_from VARCHAR(3) NOT NULL,
    currency_to VARCHAR(3) NOT NULL,
    conversion_rate DECIMAL(10,5) NOT NULL,
    port INT(5)
);