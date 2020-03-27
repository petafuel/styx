DROP FUNCTION create_consent(id text, state integer, access text, recurring_indicator boolean, valid_until timestamp without time zone, frequency_per_day integer, chosen_sca_method integer, combined_service_indicator boolean, psu_id character varying, psu_ip_address character varying, psu_ip_port integer, psu_user_agent text, psu_geo_location text, psu_id_type character varying, psu_corporate_id character varying, psu_corporate_id_type character varying, x_request_id uuid);

DROP FUNCTION delete_consent(id text);

DROP FUNCTION get_consent(id text);

DROP FUNCTION get_consent_by_x_request_id(x_request_id uuid);

DROP FUNCTION update_consent(id text, state integer, access text, recurring_indicator boolean, valid_until timestamp without time zone, frequency_per_day integer, chosen_sca_method integer, combined_service_indicator boolean, psu_id character varying, psu_ip_address character varying, psu_ip_port integer, psu_user_agent text, psu_geo_location text, psu_id_type character varying, psu_corporate_id character varying, psu_corporate_id_type character varying, x_request_id uuid);

DROP FUNCTION update_consent_state(id text, state integer);

ALTER TABLE consents
    ADD COLUMN last_action TIMESTAMP;

create or replace function create_consent(id text, state integer, access text, recurring_indicator boolean,
                                          last_action timestamp without time zone,
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
                last_action                timestamp without time zone,
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
INSERT INTO consents (id, state, access, recurring_indicator, last_action, valid_until, frequency_per_day,
                      chosen_sca_method,
                      combined_service_indicator, psu_id, psu_ip_address, psu_ip_port, psu_user_agent, psu_geo_location,
                      psu_id_type, psu_corporate_id, psu_corporate_id_type, x_request_id)
VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18) RETURNING id,
                                                                                         (SELECT name FROM consent_states WHERE consent_states.id = state) as state,
                                                                                         access,
                                                                                         recurring_indicator,
                                                                                         last_action,
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
                last_action                timestamp without time zone,
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
WHERE consents.id = $1 RETURNING id,
        (SELECT name FROM consent_states WHERE consent_states.id = state) as state,
    access,
    recurring_indicator,
    last_action,
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
                last_action                timestamp without time zone,
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
       last_action,
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
                last_action                timestamp without time zone,
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
       last_action,
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
                                          last_action timestamp without time zone,
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
                last_action                timestamp without time zone,
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
    last_action                = $5,
    valid_until                = $6,
    frequency_per_day          = $7,
    chosen_sca_method          = $8,
    combined_service_indicator = $9,
    psu_id                     = $10,
    psu_ip_address             = $11,
    psu_ip_port                = $12,
    psu_user_agent             = $13,
    psu_geo_location           = $14,
    psu_id_type                = $15,
    psu_corporate_id           = $16,
    psu_corporate_id_type      = $17,
    x_request_id               = $18,
    last_updated               = now()
WHERE consents.id = $1 RETURNING id,
        (SELECT name FROM consent_states WHERE consent_states.id = state) as state,
    access,
    recurring_indicator,
    last_action,
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
                last_action                timestamp without time zone,
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
WHERE consents.id = $1 RETURNING id,
    (SELECT name FROM consent_states WHERE consent_states.id = consents.state) as state,
    access,
    recurring_indicator,
    last_action,
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
