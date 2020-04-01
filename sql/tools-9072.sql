CREATE TABLE oauth_sessions (
  id bigserial NOT NULL constraint oauth_sessions_pk primary key,
  authorization_endpoint text NOT NULL,
  token_endpoint text NOT NULL,
  code_verifier text NOT NULL,
  state text NOT NULL UNIQUE,
  scope text NOT NULL,
  access_token text,
  token_type text,
  refresh_token text,
  expires_at timestamp,
  created_at timestamp NOT NULL,
  authorized_at timestamp
);

CREATE OR REPLACE FUNCTION create_oauth_session(authorization_endpoint text,  token_endpoint text, code_verifier text, state text, scope text)
RETURNS SETOF oauth_sessions AS $BODY$
INSERT INTO oauth_sessions (authorization_endpoint, token_endpoint, code_verifier, state, scope, created_at)
VALUES ($1, $2, $3, $4, $5, now());
SELECT * FROM oauth_sessions WHERE state = $4;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_oauth_session(state text)
RETURNS SETOF oauth_sessions AS $BODY$
SELECT * FROM oauth_sessions WHERE state = $1;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION update_oauth_session(access_token text, token_type text, refresh_token text, expiry_date timestamp, state text)
RETURNS SETOF oauth_sessions AS $BODY$
UPDATE oauth_sessions
SET access_token = $1, token_type = $2, refresh_token = $3, expires_at = $4, authorized_at = now()
WHERE state = $5;
SELECT * FROM oauth_sessions WHERE state = $5;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;
