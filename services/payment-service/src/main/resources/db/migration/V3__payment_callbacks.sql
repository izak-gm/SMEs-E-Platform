-- Enable UUID extension (once per database)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create a payment callbacks
CREATE TABLE IF NOT EXISTS payments_callbacks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    callback_provider VARCHAR(50) NOT NULL,  -- CARD or MPESA
    trans_amount NUMERIC(19,2) NOT NULL,

    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    transaction_reference VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255),
    checkout_request_id VARCHAR(255) NOT NULL UNIQUE,

    callback_status VARCHAR(50) NOT NULL,   -- PAID etc.
    paid_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );
-- Indexes
-- CREATE INDEX idx_payment_order   ON payments_callbacks(transaction_id); -- or orderId if you have order UUID column

-- CREATE INDEX idx_payment_tx    ON payments_callbacks(transaction_reference);

-- Optional: functional GIN index for JSON/metadata fields if needed in future
-- CREATE INDEX idx_payment_metadata_gin ON payments_callbacks USING gin (metadata);