CREATE TABLE accounts (
    id VARCHAR(50) PRIMARY KEY,
    type VARCHAR(20) NOT NULL, -- 'SAVINGS' ou 'CREDIT'
    balance DOUBLE PRECISION NOT NULL,
    interest_rate DOUBLE PRECISION, 
    credit_limit DOUBLE PRECISION  

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    account_id VARCHAR(50) NOT NULL,
    date VARCHAR(30) NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    new_balance DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);