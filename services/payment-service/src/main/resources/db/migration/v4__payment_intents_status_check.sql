ALTER TABLE payment_intents
DROP CONSTRAINT IF EXISTS payment_intents_status_check;

ALTER TABLE payment_intents
ADD CONSTRAINT payment_intents_status_check
CHECK (status IN ('INITIATED', 'PENDING', 'SUCCESS', 'FAILED','PAID','EXPIRED'));

ALTER TABLE payment
DROP CONSTRAINT IF EXISTS payment_status_check;

ALTER TABLE payment
ADD CONSTRAINT payment_payment_status_check
CHECK (payment_status IN ('INITIATED', 'PENDING', 'SUCCESS', 'FAILED','PAID','EXPIRED'));
