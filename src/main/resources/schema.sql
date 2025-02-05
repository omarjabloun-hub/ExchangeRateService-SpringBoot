CREATE TABLE IF NOT EXISTS currency (
    code VARCHAR(10) PRIMARY KEY,
    description VARCHAR(255),
    last_updated TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS exchange_rate (
     exchange_rate_id INT AUTO_INCREMENT PRIMARY KEY,
     currency_code VARCHAR(10) NOT NULL,
     rate_date DATE NOT NULL,
     rate_value DECIMAL(15, 6),

    CONSTRAINT fk_currency
    FOREIGN KEY (currency_code) REFERENCES currency (code)
    );
