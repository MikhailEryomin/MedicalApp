ALTER TABLE users
ALTER COLUMN email TYPE TEXT;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_lookup VARCHAR(64);

ALTER TABLE users
DROP CONSTRAINT IF EXISTS users_email_key;

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email_lookup ON users(email_lookup);
CREATE INDEX IF NOT EXISTS ix_users_email_lookup ON users(email_lookup);