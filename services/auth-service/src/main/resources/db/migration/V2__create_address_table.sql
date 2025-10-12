-- Create sequence
CREATE SEQUENCE IF NOT EXISTS public.user_sequence START 1 INCREMENT 1;

-- Create table
CREATE TABLE IF NOT EXISTS public._user (
    id BIGINT PRIMARY KEY DEFAULT nextval('public.user_sequence'),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(255) UNIQUE,
    gender VARCHAR(50),
    dob DATE,
    auth VARCHAR(50),
    password VARCHAR(255),
    is_enabled BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN DEFAULT TRUE,
    is_account_non_expired BOOLEAN DEFAULT TRUE
);

CREATE SEQUENCE IF NOT EXISTS address_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    OWNED BY NONE;

-- Create the address table
CREATE TABLE IF NOT EXISTS address (
    id INTEGER NOT NULL DEFAULT nextval('address_sequence'),
    user_id INTEGER UNIQUE,
    town VARCHAR(255),
    city VARCHAR(255),
    county VARCHAR(255),
    postal_code VARCHAR(255),
    CONSTRAINT pk_address PRIMARY KEY (id),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);
