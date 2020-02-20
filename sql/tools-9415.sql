create table public.payments
(
    id           text                    not null,
    client_token uuid                    not null
        constraint payments_tokens_id_fk
            references public.tokens,
    bic          varchar(11)             not null,
    status       varchar(4)              not null,
    created_at   timestamp default now() not null,
    updated_at   timestamp default now() not null
);

create unique index payments_id_uindex
    on public.payments (id);

create function create_payment(input_payment_id text, input_client_token uuid, input_bic character varying,
                               input_status character varying) returns payments
    security definer
    language sql
as
$$
INSERT INTO payments (id, client_token, bic, status, created_at)
VALUES ($1, $2, $3, $4, now())
RETURNING *;
$$;

create function get_payment(input_payment_id text) returns payments
    security definer
    language sql
as
$$
SELECT payments.id, payments.client_token, payments.bic, payments.status, payments.created_at, payments.updated_at
FROM payments
WHERE id = $1;
$$;

create function update_payment(input_payment_id text, input_client_token uuid, input_bic character varying,
                               input_status character varying) returns payments
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

create function update_payment_status(input_payment_id text, input_status character varying) returns payments
    security definer
    language sql
as
$$
UPDATE payments
SET status=$2,
    updated_at = now()
WHERE id = $1
RETURNING *;
$$;

create function delete_payment(input_payment_id text) returns payments
    security definer
    language sql
as
$$
DELETE
FROM payments
WHERE id = $1
RETURNING *;
$$;