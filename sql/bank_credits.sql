CREATE DATABASE IF NOT EXISTS bank_credits
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bank_credits;

CREATE TABLE IF NOT EXISTS legal_entities (
    legal_entity_id INT NOT NULL AUTO_INCREMENT,
    legal_entity_name VARCHAR(100) NOT NULL,
    ownership_type VARCHAR(50) NOT NULL,
    legal_address VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    PRIMARY KEY (legal_entity_id),
    UNIQUE KEY uq_legal_entities_name (legal_entity_name),
    UNIQUE KEY uq_legal_entities_phone (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS credit_operation_types (
    credit_type_id INT NOT NULL AUTO_INCREMENT,
    credit_type_name VARCHAR(100) NOT NULL,
    credit_conditions VARCHAR(255) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    return_period_days INT NOT NULL,
    PRIMARY KEY (credit_type_id),
    UNIQUE KEY uq_credit_type_name (credit_type_name),
    UNIQUE KEY uq_credit_type_conditions (credit_conditions, interest_rate, return_period_days),
    CHECK (interest_rate >= 0),
    CHECK (return_period_days > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS credits (
    credit_id INT NOT NULL AUTO_INCREMENT,
    legal_entity_id INT NOT NULL,
    credit_type_id INT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    issue_date DATE NOT NULL,
    actual_return_date DATE DEFAULT NULL,
    PRIMARY KEY (credit_id),
    KEY idx_credits_legal_entity_id (legal_entity_id),
    KEY idx_credits_credit_type_id (credit_type_id),
    CONSTRAINT fk_credits_legal_entity
        FOREIGN KEY (legal_entity_id) REFERENCES legal_entities (legal_entity_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_credits_credit_type
        FOREIGN KEY (credit_type_id) REFERENCES credit_operation_types (credit_type_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CHECK (amount > 0),
    CHECK (actual_return_date IS NULL OR actual_return_date >= issue_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS credit_repayments (
    repayment_id INT NOT NULL AUTO_INCREMENT,
    credit_id INT NOT NULL,
    repayment_amount DECIMAL(12,2) NOT NULL,
    repayment_date DATE NOT NULL,
    PRIMARY KEY (repayment_id),
    KEY idx_repayments_credit_id (credit_id),
    CONSTRAINT fk_repayments_credit
        FOREIGN KEY (credit_id) REFERENCES credits (credit_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CHECK (repayment_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS fines (
    fine_id INT NOT NULL AUTO_INCREMENT,
    credit_id INT NOT NULL,
    fine_amount DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (fine_id),
    KEY idx_fines_credit_id (credit_id),
    CONSTRAINT fk_fines_credit
        FOREIGN KEY (credit_id) REFERENCES credits (credit_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CHECK (fine_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS system_users (
    user_id INT NOT NULL AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_system_users_login (login),
    CHECK (role IN ('ADMIN', 'USER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
