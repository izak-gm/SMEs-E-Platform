CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--
CREATE TABLE IF NOT EXISTS payment_intents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID,
    buyer_id UUID,
    store_id UUID,
    total_amount NUMERIC(12,2),
    currency VARCHAR(20) CHECK (currency IN ('KES')),
    status VARCHAR(20) CHECK (status IN ('PENDING','SUCCESS','FAILED')),
    metadata JSONB,
    expires_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
