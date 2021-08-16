
alter table client_apps
    add restrictions json;

alter table tokens
    add usages integer default 0;

alter table tokens
    add client_reference varchar(64);

create or replace function create_token(client_master_token character varying, access_token character varying, service character varying, expires_in integer, client_reference character varying) returns tokens
    security definer
    language sql
as $$
INSERT INTO tokens (client_master_token, id, service, expires_in, client_reference)
VALUES ($1, $2, $3, $4, $5)
RETURNING *;
$$;

drop function if exits create_token(client_master_token character varying, access_token character varying, service character varying, expires_in integer);

create or replace function update_token_usage(token character varying) returns void
    security definer
    language sql
as $$
UPDATE tokens
SET last_used_on = now(), usages = usages + 1
WHERE id = $1;
$$;

