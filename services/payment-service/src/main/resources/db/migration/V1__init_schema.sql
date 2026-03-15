-- Enable UUID extension (once per database)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create payment table
CREATE TABLE IF NOT EXISTS public.payment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID,
    buyer_id UUID,
    phone_number VARCHAR(255),
    transaction_reference VARCHAR(255) UNIQUE,
    amount NUMERIC(12,2),
    method VARCHAR(20) CHECK (method IN ('CARD','MPESA')),
    status VARCHAR(20) CHECK (status IN ('PENDING','SUCCESS','FAILED'))
    -- created_at TIMESTAMP DEFAULT now() NOT NULL,
    -- updated_at TIMESTAMP DEFAULT now() NOT NULL
);
CREATE INDEX idx_payment_order_id ON payment(order_id);
CREATE INDEX idx_payment_buyer_id ON payment(buyer_id);