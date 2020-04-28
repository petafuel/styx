--
-- Make tokens API Conventions compliant
--

-- remove fk constraints
alter table tokens
    drop constraint if exists client_app_id_fk;
alter table consents
    drop constraint if exists consents_tokens_id_fk;
alter table payments
    drop constraint if exists payments_tokens_id_fk;

-- token length 64 characters for access and master tokens
alter table tokens
    alter column id type varchar(64) using id::varchar(64);
alter table tokens
    alter column client_master_token type varchar(64) using client_master_token::varchar(64);
alter table client_apps
    alter column master_token type varchar(64) using master_token::varchar(64);
alter table consents
    alter column token type varchar(64) using token::varchar(64);
alter table payments
    alter column client_token type varchar(64) using client_token::varchar(64);

-- re-add fk from access token to master token
alter table tokens
    add constraint client_app_id_fk
        foreign key (client_master_token) references client_apps;

-- re-add access token fk
alter table consents
    add constraint consents_tokens_id_fk
        foreign key (token) references tokens;

alter table payments
    add constraint payments_tokens_id_fk
        foreign key (client_token) references tokens;


alter table tokens
    add IF NOT EXISTS service varchar(6) not null;

alter table tokens
    add IF NOT EXISTS expires_in int not null default 300;

alter table tokens
    add IF NOT EXISTS last_used_on timestamp default null;

--
-- Change functions with new varchar tokens
--

drop function if exists create_token(uuid, uuid);
drop function if exists create_token(uuid, uuid, varchar, integer);

create or replace function create_token(client_master_token varchar, access_token varchar, service character varying,
                                        expires_in integer) returns tokens
    security definer
    language sql
as
$$
INSERT INTO tokens (client_master_token, id, service, expires_in)
VALUES ($1, $2, $3, $4)
RETURNING *;
$$;

--
--
--

drop function if exists get_consent_by_token(uuid);

create or replace function get_consent_by_token(token varchar) returns SETOF consents
    security definer
    language sql
as
$$
SELECT DISTINCT c.*
FROM consents c
         INNER JOIN tokens t on t.id = c.token
WHERE c.token = $1
  AND t.valid = TRUE;
$$;

--
--
--

drop function if exists get_token(uuid);

create or replace function get_token(token varchar) returns SETOF tokens
    security definer
    language sql
as
$$
SELECT *
FROM tokens
WHERE id = $1;
$$;

--
--
--

drop function if exists set_token_validity(uuid, boolean);
drop function if exists set_token_validity(varchar, boolean);

create or replace function set_token_validity(token varchar, valid boolean) returns SETOF tokens
    security definer
    language sql
as
$$
UPDATE tokens
SET valid = $2
WHERE id = $1;
SELECT *
FROM tokens
WHERE id = $1;
$$;

--
--
--

drop function if exists update_token_usage(uuid);

create or replace function update_token_usage(token varchar) returns void
    security definer
    language sql
as
$$
UPDATE tokens
SET last_used_on = now()
WHERE id = $1;
$$;

--
--
--

drop function if exists create_payment(text, uuid, varchar, varchar);
drop function if exists create_payment(text, varchar, varchar, varchar);

create or replace  function create_payment(input_payment_id text, input_client_token varchar, input_bic character varying,
                                           input_status character varying) returns SETOF payments
    security definer
    language sql
as
$$
INSERT INTO payments (id, client_token, bic, status, created_at)
VALUES ($1, $2, $3, $4, now())
RETURNING *;
$$;

--
--
--

drop function if exists get_client_app(uuid);
drop function if exists get_client_app(varchar);

create or replace  function get_client_app(master_token varchar) returns SETOF client_apps
    security definer
    language sql
as
$$
SELECT *
FROM client_apps
WHERE client_apps.master_token = $1;
$$;

--
--
--

drop function if exists update_payment(text, uuid, varchar, varchar);
drop function if exists update_payment(text, varchar, varchar, varchar);

create or replace function update_payment(input_payment_id text, input_client_token varchar, input_bic character varying,
                                          input_status character varying) returns SETOF payments
    security definer
    language sql
as
$$
UPDATE payments
SET client_token=$2,
    bic=$3,
    status=$4,
    updated_at  = now()
WHERE id = $1
RETURNING *;
$$;
