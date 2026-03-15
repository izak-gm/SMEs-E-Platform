-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create sequence for address table
CREATE SEQUENCE IF NOT EXISTS address_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    OWNED BY NONE;

-- Create address table
CREATE TABLE IF NOT EXISTS address (
    id INTEGER PRIMARY KEY DEFAULT nextval('address_sequence'),
    user_id UUID UNIQUE,  -- This matches _user.id which is UUID
    town VARCHAR(255),
    city VARCHAR(255),
    county VARCHAR(255),
    postal_code VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_address_user
        FOREIGN KEY (user_id)
        REFERENCES public._user(id)
        ON DELETE CASCADE
);

-- Link sequence to address.id
ALTER SEQUENCE address_sequence OWNED BY address.id;

-- Create index for better performance on foreign key lookups
CREATE INDEX IF NOT EXISTS idx_address_user_id ON address(user_id);