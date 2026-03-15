-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===============================
-- NOTIFICATION TABLE
-- ===============================
CREATE TABLE IF NOT EXISTS notification (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    topic VARCHAR(255),
    email VARCHAR(255),
    message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
