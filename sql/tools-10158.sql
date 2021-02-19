--############################################################
-- SQL vor Release
--############################################################
--
--New enum type for payment service
--
DROP TYPE IF EXISTS PAYMENT_SERVICE;
CREATE TYPE PAYMENT_SERVICE AS ENUM (
    'PAYMENTS',
    'BULK_PAYMENTS',
    'PERIODIC_PAYMENTS'
    );

--
--New enum type for payment product
--
DROP TYPE IF EXISTS PAYMENT_PRODUCT;
CREATE TYPE PAYMENT_PRODUCT AS ENUM (
    'SEPA_CREDIT_TRANSFERS',
    'INSTANT_SEPA_CREDIT_TRANSFERS',
    'TARGET_2_PAYMENTS',
    'CROSS_BORDER_CREDIT_TRANSFERS',
    'PAIN_001_SEPA_CREDIT_TRANSFERS',
    'PAIN_001_INSTANT_SEPA_CREDIT_TRANSFERS',
    'PAIN_001_TARGET_2_PAYMENTS',
    'PAIN_001_CROSS_BORDER_CREDIT_TRANSFERS'
    );

--
--Add service column as custom enum type PAYMENT_SERVICE
--
alter table payments
    add service PAYMENT_SERVICE default 'PAYMENTS'::PAYMENT_SERVICE not null;
--
--Add product column as custom enum type PAYMENT_PRODUCT
--
alter table payments
    add product PAYMENT_PRODUCT default 'SEPA_CREDIT_TRANSFERS'::PAYMENT_PRODUCT not null;

--
--Save payment_product and payment_service
--
create or replace function create_payment(input_id character varying, input_payment_id text,
                                          input_client_token character varying,
                                          input_bic character varying, input_status character varying,
                                          input_service character varying,
                                          input_product character varying) returns SETOF payments
    security definer
    language sql
as
$$
INSERT INTO payments (id, payment_id, client_token, bic, status, created_at, service, product)
VALUES (input_id, input_payment_id, input_client_token, input_bic, input_status, now(), input_service::PAYMENT_SERVICE,
        input_product::PAYMENT_PRODUCT)
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id, payments.service, payments.product;
$$;

--
--Return new columns(service, product) on delete
--
DROP FUNCTION IF EXISTS delete_payment_by_id(text);
create or replace function delete_payment_by_id(input_id text) returns payments
    security definer
    language sql
as
$$
DELETE
FROM payments
WHERE payments.id = input_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id, payments.service, payments.product;
$$;

--
--Return new columns(service, product) on delete
--
DROP FUNCTION IF EXISTS delete_payment_by_payment_id(text);
create or replace function delete_payment_by_payment_id(input_payment_id text) returns payments
    security definer
    language sql
as
$$
DELETE
FROM payments
WHERE payments.payment_id = input_payment_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id, payments.service, payments.product;
$$;


--
--Return new columns(service, product)
--
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
       payments.id,
       payments.service,
       payments.product
FROM payments
WHERE payments.id = input_id;
$$;


--
--Return new columns(service, product)
--
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
       payments.id,
       payments.service,
       payments.product
FROM payments
WHERE payments.payment_id = input_payment_id;
$$;

--
--Return new columns(service, product)
--
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
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id, payments.service, payments.product;
$$;

--
--Return new columns(service, product)
--
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
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id, payments.service, payments.product;
$$;


--
--Return new columns(service, product)
--
create or replace function update_payment_status_by_id(input_id character varying, input_status character varying) returns payments
    security definer
    language sql
as
$$
UPDATE payments
SET status     = input_status,
    updated_at = now()
WHERE payments.id = input_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id, payments.service, payments.product;
$$;

--
--Return new columns(service, product)
--
create or replace function update_payment_status_by_payment_id(input_payment_id text, input_status character varying) returns payments
    security definer
    language sql
as
$$
UPDATE payments
SET status     = input_status,
    updated_at = now()
WHERE payments.payment_id = input_payment_id
RETURNING payments.payment_id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at, payments.id, payments.service, payments.product;
$$;

--############################################################
-- SQL nach Release
--############################################################
--
--Remove old sql function
--
DROP FUNCTION IF EXISTS create_payment(varchar, text, varchar, varchar, varchar);