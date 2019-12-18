CREATE TABLE client_apps (
  master_token UUID CONSTRAINT client_apps_pk PRIMARY KEY,
  name text NOT NULL UNIQUE,
  redirect_url text NOT NULL,
  enabled boolean DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE tokens
ADD COLUMN client_master_token UUID NOT NULL CONSTRAINT client_app_id_fk REFERENCES client_apps,
ADD COLUMN valid BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT now(),
ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT now();

CREATE OR REPLACE FUNCTION get_client_app (master_token UUID)
RETURNS SETOF client_apps AS $BODY$
SELECT * FROM client_apps WHERE master_token = $1;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION create_token (client_master_token UUID, access_token UUID)
RETURNS SETOF tokens AS $BODY$
INSERT INTO tokens (client_master_token, id) VALUES ($1, $2) RETURNING *;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION set_token_validity (token uuid, valid BOOLEAN)
RETURNS SETOF tokens AS $BODY$
UPDATE tokens SET valid = $2 WHERE id = $1;
SELECT * FROM tokens WHERE id = $1;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_consent_by_token (token uuid)
RETURNS SETOF consents AS $BODY$
SELECT DISTINCT c.* FROM consents c
INNER JOIN tokens t on t.id = c.token
WHERE c.token = $1 AND t.valid = TRUE;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;

CREATE OR REPLACE FUNCTION get_token (token uuid)
RETURNS SETOF tokens AS $BODY$
SELECT * FROM tokens WHERE id = $1;
$BODY$ LANGUAGE SQL VOLATILE SECURITY DEFINER;