DROP TABLE IF EXISTS Account;

CREATE TABLE Account(
    account_number INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    token VARCHAR(34) NOT NULL UNIQUE,
    es_index_name VARCHAR(42) NOT NULL UNIQUE,
    PRIMARY KEY(account_number)
    )