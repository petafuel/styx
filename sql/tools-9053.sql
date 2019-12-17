--
-- Definitions
--

-- auto-generated definition
create table consent_states
(
    id   serial      not null
        constraint consent_state_pk
            primary key,
    name varchar(50) not null
);

create unique index consent_state_id_uindex
    on consent_states (id);

create unique index consent_state_name_uindex
    on consent_states (name);

INSERT INTO public.consent_states (id, name)
VALUES (1, 'received');
INSERT INTO public.consent_states (id, name)
VALUES (2, 'rejected');
INSERT INTO public.consent_states (id, name)
VALUES (3, 'partiallyAuthorised');
INSERT INTO public.consent_states (id, name)
VALUES (4, 'valid');
INSERT INTO public.consent_states (id, name)
VALUES (5, 'revokedByPsu');
INSERT INTO public.consent_states (id, name)
VALUES (6, 'expired');
INSERT INTO public.consent_states (id, name)
VALUES (7, 'terminatedByTpp');

-- auto-generated definition
create table sca_methods
(
    id   serial      not null
        constraint sca_methods_pk
            primary key,
    name varchar(50) not null
);

create unique index sca_methods_id_uindex
    on sca_methods (id);

create unique index sca_methods_name_uindex
    on sca_methods (name);

INSERT INTO public.sca_methods (id, name)
VALUES (1, 'DECOUPLED');
INSERT INTO public.sca_methods (id, name)
VALUES (2, 'EMBEDDED');
INSERT INTO public.sca_methods (id, name)
VALUES (3, 'REDIRECT');
INSERT INTO public.sca_methods (id, name)
VALUES (4, 'OAUTH2');
INSERT INTO public.sca_methods (id, name)
VALUES (5, 'REQUIRE_AUTHORISATION_RESOURCE');

-- auto-generated definition
create table tokens
(
    id uuid not null
        constraint tokens_pk
            primary key
);

comment on table tokens is 'dummy';

create unique index tokens_id_uindex
    on tokens (id);

create table consents
(
    id                         text                       not null
        constraint consents_pk
            primary key,
    state                      integer      default 1     not null
        constraint consents_consent_states_id_fk
            references consent_states,
    access                     text                       not null,
    recurring_indicator        boolean      default false not null,
    valid_until                timestamp                  not null,
    last_updated               timestamp    default now() not null,
    frequency_per_day          integer                    not null,
    chosen_sca_method          integer                    not null
        constraint consents_sca_methods_id_fk
            references sca_methods,
    combined_service_indicator boolean                    not null,
    psu_id                     varchar(150)               not null,
    psu_ip_address             varchar(50)  default NULL::character varying,
    psu_ip_port                integer,
    psu_user_agent             text,
    psu_geo_location           text,
    psu_id_type                varchar(255) default NULL::character varying,
    psu_corporate_id           varchar(255) default NULL::character varying,
    psu_corporate_id_type      varchar(255) default NULL::character varying,
    x_request_id               uuid                       not null,
    token                      uuid
        constraint consents_tokens_id_fk
            references tokens,
    created_at                 timestamp    default now() not null
);

create unique index consents_consent_id_uindex
    on consents (id);

create unique index consents_x_request_id_uindex
    on consents (x_request_id);

--
--  Functions
--
create or replace function create_consent(id text, state integer, access text, recurring_indicator boolean,
                                          valid_until timestamp without time zone, frequency_per_day integer,
                                          chosen_sca_method integer, combined_service_indicator boolean,
                                          psu_id character varying,
                                          psu_ip_address character varying, psu_ip_port integer, psu_user_agent text,
                                          psu_geo_location text, psu_id_type character varying,
                                          psu_corporate_id character varying,
                                          psu_corporate_id_type character varying, x_request_id uuid)
    returns TABLE
            (
                id                         text,
                state                      character varying,
                access                     text,
                recurring_indicator        boolean,
                valid_until                timestamp without time zone,
                last_updated               timestamp without time zone,
                frequency_per_day          integer,
                combined_service_indicator boolean,
                chosen_sca_method          character varying,
                psu_id                     character varying,
                psu_id_type                character varying,
                psu_ip_address             character varying,
                psu_ip_port                integer,
                psu_user_agent             text,
                psu_geo_location           text,
                psu_corporate_id           character varying,
                psu_corporate_id_type      character varying,
                x_request_id               uuid,
                created_at                 timestamp
            )
    security definer
    language sql
as
$$
INSERT INTO consents (id, state, access, recurring_indicator, valid_until, frequency_per_day, chosen_sca_method,
                      combined_service_indicator, psu_id, psu_ip_address, psu_ip_port, psu_user_agent, psu_geo_location,
                      psu_id_type, psu_corporate_id, psu_corporate_id_type, x_request_id)
VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17)

RETURNING id,
        (SELECT name FROM consent_states WHERE consent_states.id = state) as state,
    access,
    recurring_indicator,
    valid_until,
    last_updated,
    frequency_per_day,
    combined_service_indicator,
        (SELECT name FROM sca_methods WHERE sca_methods.id = chosen_sca_method) as chosen_sca_method,
    psu_id,
    psu_id_type,
    psu_ip_address,
    psu_ip_port,
    psu_user_agent,
    psu_geo_location,
    psu_corporate_id,
    psu_corporate_id_type,
    x_request_id,
    created_at;
$$;


create or replace function delete_consent(id text)
    returns TABLE
            (
                id                         text,
                state                      character varying,
                access                     text,
                recurring_indicator        boolean,
                valid_until                timestamp without time zone,
                last_updated               timestamp without time zone,
                frequency_per_day          integer,
                combined_service_indicator boolean,
                chosen_sca_method          character varying,
                psu_id                     character varying,
                psu_id_type                character varying,
                psu_ip_address             character varying,
                psu_ip_port                integer,
                psu_user_agent             text,
                psu_geo_location           text,
                psu_corporate_id           character varying,
                psu_corporate_id_type      character varying,
                x_request_id               uuid,
                created_at                 timestamp
            )
    security definer
    language sql
as
$$
DELETE
FROM consents
WHERE consents.id = $1
RETURNING id,
        (SELECT name FROM consent_states WHERE consent_states.id = state) as state,
    access,
    recurring_indicator,
    valid_until,
    last_updated,
    frequency_per_day,
    combined_service_indicator,
        (SELECT name FROM sca_methods WHERE sca_methods.id = chosen_sca_method) as chosen_sca_method,
    psu_id,
    psu_id_type,
    psu_ip_address,
    psu_ip_port,
    psu_user_agent,
    psu_geo_location,
    psu_corporate_id,
    psu_corporate_id_type,
    x_request_id,
    created_at;

$$;

create or replace function get_consent(id text)
    returns TABLE
            (
                id                         text,
                access                     text,
                recurring_indicator        boolean,
                valid_until                timestamp without time zone,
                last_updated               timestamp without time zone,
                frequency_per_day          integer,
                combined_service_indicator boolean,
                psu_id                     character varying,
                psu_id_type                character varying,
                psu_ip_address             character varying,
                psu_ip_port                integer,
                psu_user_agent             text,
                psu_geo_location           text,
                psu_corporate_id           character varying,
                psu_corporate_id_type      character varying,
                state                      character varying,
                chosen_sca_method          character varying,
                x_request_id               uuid,
                created_at                 timestamp
            )
    security definer
    language sql
as
$$
SELECT consents.id,
       access,
       recurring_indicator,
       valid_until,
       last_updated,
       frequency_per_day,
       combined_service_indicator,
       psu_id,
       psu_id_type,
       psu_ip_address,
       psu_ip_port,
       psu_user_agent,
       psu_geo_location,
       psu_corporate_id,
       psu_corporate_id_type,
       cs.name as state,
       sm.name as chosen_sca_method,
       x_request_id,
       created_at
FROM consents
         JOIN consent_states cs on consents.state = cs.id
         JOIN sca_methods sm on consents.chosen_sca_method = sm.id
WHERE consents.id = $1;
$$;

create or replace function get_consent_by_x_request_id(x_request_id uuid)
    returns TABLE
            (
                id                         text,
                access                     text,
                recurring_indicator        boolean,
                valid_until                timestamp without time zone,
                last_updated               timestamp without time zone,
                frequency_per_day          integer,
                combined_service_indicator boolean,
                psu_id                     character varying,
                psu_id_type                character varying,
                psu_ip_address             character varying,
                psu_ip_port                integer,
                psu_user_agent             text,
                psu_geo_location           text,
                psu_corporate_id           character varying,
                psu_corporate_id_type      character varying,
                state                      character varying,
                chosen_sca_method          character varying,
                x_request_id               uuid,
                created_at                 timestamp
            )
    security definer
    language sql
as
$$
SELECT consents.id,
       access,
       recurring_indicator,
       valid_until,
       last_updated,
       frequency_per_day,
       combined_service_indicator,
       psu_id,
       psu_id_type,
       psu_ip_address,
       psu_ip_port,
       psu_user_agent,
       psu_geo_location,
       psu_corporate_id,
       psu_corporate_id_type,
       cs.name as state,
       sm.name as chosen_sca_method,
       x_request_id,
       created_at
FROM consents
         JOIN consent_states cs on consents.state = cs.id
         JOIN sca_methods sm on consents.chosen_sca_method = sm.id
WHERE consents.x_request_id = $1;
$$;

create or replace function update_consent(id text, state integer, access text, recurring_indicator boolean,
                                          valid_until timestamp without time zone, frequency_per_day integer,
                                          chosen_sca_method integer, combined_service_indicator boolean,
                                          psu_id character varying,
                                          psu_ip_address character varying, psu_ip_port integer, psu_user_agent text,
                                          psu_geo_location text, psu_id_type character varying,
                                          psu_corporate_id character varying,
                                          psu_corporate_id_type character varying, x_request_id uuid)
    returns TABLE
            (
                id                         text,
                state                      character varying,
                access                     text,
                recurring_indicator        boolean,
                valid_until                timestamp without time zone,
                last_updated               timestamp without time zone,
                frequency_per_day          integer,
                combined_service_indicator boolean,
                chosen_sca_method          character varying,
                psu_id                     character varying,
                psu_id_type                character varying,
                psu_ip_address             character varying,
                psu_ip_port                integer,
                psu_user_agent             text,
                psu_geo_location           text,
                psu_corporate_id           character varying,
                psu_corporate_id_type      character varying,
                x_request_id               uuid,
                created_at                 timestamp
            )
    security definer
    language sql
as
$$
UPDATE consents
SET state                      = $2,
    access                     = $3,
    recurring_indicator        = $4,
    valid_until                = $5,
    frequency_per_day          = $6,
    chosen_sca_method          = $7,
    combined_service_indicator = $8,
    psu_id                     = $9,
    psu_ip_address             = $10,
    psu_ip_port                = $11,
    psu_user_agent             = $12,
    psu_geo_location           = $13,
    psu_id_type                = $14,
    psu_corporate_id           = $15,
    psu_corporate_id_type      = $16,
    x_request_id               = $17,
    last_updated               = now()
WHERE consents.id = $1
RETURNING id,
        (SELECT name FROM consent_states WHERE consent_states.id = state) as state,
    access,
    recurring_indicator,
    valid_until,
    last_updated,
    frequency_per_day,
    combined_service_indicator,
        (SELECT name FROM sca_methods WHERE sca_methods.id = chosen_sca_method) as chosen_sca_method,
    psu_id,
    psu_id_type,
    psu_ip_address,
    psu_ip_port,
    psu_user_agent,
    psu_geo_location,
    psu_corporate_id,
    psu_corporate_id_type,
    x_request_id,
    created_at;
$$;

create or replace function update_consent_state(id text, state integer)
    returns TABLE
            (
                id                         text,
                state                      character varying,
                access                     text,
                recurring_indicator        boolean,
                valid_until                timestamp without time zone,
                last_updated               timestamp without time zone,
                frequency_per_day          integer,
                combined_service_indicator boolean,
                chosen_sca_method          character varying,
                psu_id                     character varying,
                psu_id_type                character varying,
                psu_ip_address             character varying,
                psu_ip_port                integer,
                psu_user_agent             text,
                psu_geo_location           text,
                psu_corporate_id           character varying,
                psu_corporate_id_type      character varying,
                x_request_id               uuid,
                created_at                 timestamp
            )
    security definer
    language sql
as
$$
UPDATE consents
SET state        = $2,
    last_updated = now()
WHERE consents.id = $1
RETURNING id,
    (SELECT name FROM consent_states WHERE consent_states.id = consents.state) as state,
    access,
    recurring_indicator,
    valid_until,
    last_updated,
    frequency_per_day,
    combined_service_indicator,
        (SELECT name FROM sca_methods WHERE sca_methods.id = chosen_sca_method) as chosen_sca_method,
    psu_id,
    psu_id_type,
    psu_ip_address,
    psu_ip_port,
    psu_user_agent,
    psu_geo_location,
    psu_corporate_id,
    psu_corporate_id_type,
    x_request_id,
    created_at;
$$;
