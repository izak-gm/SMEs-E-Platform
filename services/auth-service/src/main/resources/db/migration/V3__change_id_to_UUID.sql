-- Enable UUID extension (needed for uuid_generate_v4())
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Step 1: Add a new UUID column
ALTER TABLE public._user
ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- Step 2: If you want to keep old ID temporarily, optional
-- UPDATE public._user SET uuid_id = uuid_generate_v4();

-- Step 3: Drop old primary key constraint
ALTER TABLE public._user
DROP CONSTRAINT _user_pkey;

-- Step 4: Set the new UUID column as PRIMARY KEY
ALTER TABLE public._user
ADD CONSTRAINT _user_pkey PRIMARY KEY (uuid_id);

-- Step 5: Optionally drop old INT id column
ALTER TABLE public._user
DROP COLUMN id;

-- Step 6: Rename uuid_id to id (so your app still uses `id`)
ALTER TABLE public._user
RENAME COLUMN uuid_id TO id;
