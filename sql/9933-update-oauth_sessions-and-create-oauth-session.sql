ALTER TABLE oauth_sessions DROP COLUMN IF EXISTS id;
ALTER TABLE oauth_sessions ADD COLUMN IF NOT EXISTS id uuid;

-- this should be executed once only
UPDATE oauth_sessions 
SET id = state::uuid
WHERE id IS null;

ALTER TABLE oauth_sessions ADD CONSTRAINT pk_uuid PRIMARY KEY (id);

ALTER FUNCTION get_oauth_session(state text) 
RENAME TO get_oauth_session_by_state;

CREATE OR REPLACE FUNCTION get_oauth_session_by_id(id uuid)
RETURNS SETOF oauth_sessions AS $BODY$
SELECT * FROM oauth_sessions WHERE oauth_sessions.id = $1;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

DROP FUNCTION IF EXISTS create_oauth_session(authorization_endpoint text,  token_endpoint text, code_verifier text, state text, scope text);

CREATE OR REPLACE FUNCTION create_oauth_session(authorization_endpoint text,  token_endpoint text, code_verifier text, state text, scope text, id uuid)
RETURNS SETOF oauth_sessions AS $BODY$
INSERT INTO oauth_sessions (authorization_endpoint, token_endpoint, code_verifier, state, scope, created_at, id)
VALUES ($1, $2, $3, $4, $5, now(), $6);
SELECT * FROM oauth_sessions WHERE oauth_sessions.id = $6;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;
