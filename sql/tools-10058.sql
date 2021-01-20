alter table payments
    rename column id to payment_id;

alter table payments
    add id varchar(40) DEFAULT 'notset';

--
--
--
DROP FUNCTION IF EXISTS create_payment(text, character varying, character varying, character varying);

create or replace function create_payment(input_id character varying, input_payment_id text,
                                          input_client_token character varying,
                                          input_bic character varying,
                                          input_status character varying) returns SETOF payments
    security definer
    language sql
as
$$
INSERT INTO payments (id, payment_id, client_token, bic, status, created_at)
VALUES (input_id, input_payment_id, input_client_token, input_bic, input_status, now())
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id;
$$;

--
--
--
DROP FUNCTION IF EXISTS get_payment(text);
DROP FUNCTION IF EXISTS get_payment_by_payment_id(text);

create or replace function get_payment_by_payment_id(input_payment_id text) returns payments
    security definer
    language sql
as
$$
SELECT payments.payment_id,
       payments.client_token,
       payments.bic,
       payments.status,
       payments.created_at,
       payments.updated_at,
       payments.id
FROM payments
WHERE payments.payment_id = input_payment_id;
$$;

--
--
--
DROP FUNCTION IF EXISTS get_payment(text);
DROP FUNCTION IF EXISTS get_payment_by_id(character varying);

create or replace function get_payment_by_id(input_id character varying) returns payments
    security definer
    language sql
as
$$
SELECT payments.payment_id,
       payments.client_token,
       payments.bic,
       payments.status,
       payments.created_at,
       payments.updated_at,
       payments.id
FROM payments
WHERE payments.id = input_id;
$$;

--
--
--
DROP FUNCTION IF EXISTS update_payment_status(text, character varying);
DROP FUNCTION IF EXISTS update_payment_status_by_payment_id(text, character varying);

create or replace function update_payment_status_by_payment_id(input_payment_id text, input_status character varying) returns payments
    security definer
    language sql
as
$$
UPDATE payments
SET status     = input_status,
    updated_at = now()
WHERE payments.payment_id = input_payment_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id;
$$;

--
--
--
DROP FUNCTION IF EXISTS update_payment_status(text, character varying);
DROP FUNCTION IF EXISTS update_payment_status_by_id(text, character varying);

create or replace function update_payment_status_by_id(input_id character varying, input_status character varying) returns payments
    security definer
    language sql
as
$$
UPDATE payments
SET status     = input_status,
    updated_at = now()
WHERE payments.id = input_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id;
$$;

--
--
--
DROP FUNCTION IF EXISTS update_payment(text, character varying, character varying, character varying);
DROP FUNCTION IF EXISTS update_payment_by_payment_id(text, character varying, character varying, character varying);

create or replace function update_payment_by_payment_id(input_payment_id text, input_client_token character varying,
                                                        input_bic character varying,
                                                        input_status character varying) returns SETOF payments
    security definer
    language sql
as
$$
UPDATE payments
SET client_token = input_client_token,
    bic          = input_bic,
    status       = input_status,
    updated_at   = now()
WHERE payments.payment_id = input_payment_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id;
$$;
--
--
--
DROP FUNCTION IF EXISTS update_payment(text, character varying, character varying, character varying);
DROP FUNCTION IF EXISTS update_payment_by_id(text, character varying, character varying, character varying);

create or replace function update_payment_by_id(input_id text, input_client_token character varying,
                                                input_bic character varying,
                                                input_status character varying) returns SETOF payments
    security definer
    language sql
as
$$
UPDATE payments
SET client_token = input_client_token,
    bic          = input_bic,
    status       = input_status,
    updated_at   = now()
WHERE payments.id = input_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id;
$$;

--
--
--
DROP FUNCTION IF EXISTS delete_payment(text);
DROP FUNCTION IF EXISTS delete_payment_by_payment_id(text);

CREATE OR REPLACE FUNCTION delete_payment_by_payment_id(input_payment_id text) returns payments
    security definer
    language sql
as
$$
DELETE
FROM payments
WHERE payments.payment_id = input_payment_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id;
$$;

--
--
--
DROP FUNCTION IF EXISTS delete_payment(text);
DROP FUNCTION IF EXISTS delete_payment_by_id(text);

CREATE OR REPLACE FUNCTION delete_payment_by_id(input_id text) returns payments
    security definer
    language sql
as
$$
DELETE
FROM payments
WHERE payments.id = input_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id;
$$;