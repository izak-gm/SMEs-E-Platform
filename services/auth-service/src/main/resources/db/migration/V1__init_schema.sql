-- Create sequence
CREATE SEQUENCE IF NOT EXISTS public.user_sequence START 1 INCREMENT 1;

-- Create table
CREATE TABLE IF NOT EXISTS public._user (
    id INT PRIMARY KEY DEFAULT nextval('public.user_sequence'),
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
