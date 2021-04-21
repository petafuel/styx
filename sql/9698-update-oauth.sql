DROP FUNCTION IF EXISTS update_oauth_session(
    access_token text,
    token_type text,
    refresh_token text,
    expires_at timestamp
);

ALTER TABLE oauth_sessions
    ADD COLUMN if not exists access_token_expires_at timestamp,
    ADD COLUMN if not exists refresh_token_expires_at timestamp;

UPDATE oauth_sessions
    SET
    access_token_expires_at = expires_at,
    refresh_token_expires_at = expires_at + interval '90 days'
    WHERE expires_at IS NOT NULL;

ALTER TABLE oauth_sessions
    DROP COLUMN if exists expires_at;

CREATE OR REPLACE FUNCTION update_oauth_session(
    access_token text,
    token_type text,
    refresh_token text,
    access_token_expires_at timestamp,
    refresh_token_expires_at timestamp,
    state text)
RETURNS SETOF oauth_sessions AS $BODY$
UPDATE oauth_sessions
SET access_token = $1, token_type = $2, refresh_token = $3, access_token_expires_at = $4, refresh_token_expires_at = $5, authorized_at = now()
WHERE state = $6;
SELECT * FROM oauth_sessions WHERE state = $6;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;
