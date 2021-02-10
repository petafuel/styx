-- add the x_request_id column, which will relate a OAuthSession to a Consent or a Payment
ALTER TABLE oauth_sessions ADD COLUMN IF NOT EXISTS x_request_id uuid;

-- column should not be null, that's why we set a random value to the existing records
UPDATE oauth_sessions
SET x_request_id = id
WHERE x_request_id IS null;

-- SQL function to fetch a OAuthSession by X-Request-Id
CREATE OR REPLACE FUNCTION get_oauth_session_by_x_request_id(x_request_id uuid)
RETURNS SETOF oauth_sessions AS $BODY$
SELECT * FROM oauth_sessions WHERE x_request_id = $1;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

-- The create function will be extended by one more parameter. The X-Request-Id should be also saved during the creation of a OAuthSession
DROP FUNCTION IF EXISTS create_oauth_session(authorization_endpoint text,  token_endpoint text, code_verifier text, state text, scope text, id uuid);

CREATE OR REPLACE FUNCTION create_oauth_session(authorization_endpoint text,  token_endpoint text, code_verifier text, state text, scope text, id uuid, x_request_id uuid)
RETURNS SETOF oauth_sessions AS $BODY$
INSERT INTO oauth_sessions (authorization_endpoint, token_endpoint, code_verifier, state, scope, created_at, id, x_request_id)
VALUES ($1, $2, $3, $4, $5, now(), $6, $7);
SELECT * FROM oauth_sessions WHERE id = $6;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

-- The update_oauth_session function does not require changes after adding the X-Request-Id column