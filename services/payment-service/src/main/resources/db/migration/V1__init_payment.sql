-- Enable UUID extension (once per database)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create payment table
CREATE TABLE IF NOT EXISTS public.payment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID,
    buyer_id UUID,
    phone_number VARCHAR(255),
    transaction_reference VARCHAR(255) UNIQUE,
    amount FLOAT,
    method VARCHAR(50),
    status VARCHAR(50)
);
