CREATE TYPE consent_status AS ENUM ('received', 'rejected', 'partiallyAuthorised', 'valid', 'revokedByPsu', 'expired', 'terminatedByTpp');

/*
Old consent_states tabel
1	"received"
2	"rejected"
3	"partiallyAuthorised"
4	"valid"
5	"revokedByPsu"
6	"expired"
7	"terminatedByTpp"
*/

ALTER TABLE consents
DROP CONSTRAINT consents_consent_states_id_fk,
ALTER COLUMN state DROP DEFAULT,
ALTER COLUMN state SET DATA TYPE consent_status
USING (CASE
WHEN state = 1 THEN 'received'::consent_status
WHEN state = 3 THEN 'partiallyAuthorised'::consent_status
WHEN state = 4 THEN 'valid'::consent_status
WHEN state = 5 THEN 'revokedByPsu'::consent_status
WHEN state = 6 THEN 'expired'::consent_status
WHEN state = 7 THEN 'terminatedByTpp'::consent_status
ELSE 'rejected'::consent_status
END),
ALTER COLUMN state SET DEFAULT 'received'::consent_status;

DROP FUNCTION create_consent(text, integer, text, boolean, timestamp without time zone, timestamp without time zone, 
							 integer, integer, boolean, character varying, character varying, integer, text, text, 
							 character varying, character varying, character varying, uuid);

create or replace function create_consent(id text, state character varying, access text, recurring_indicator boolean,
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
                state                      consent_status,
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
VALUES ($1, $2::consent_status, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18) 
RETURNING id,
          state,
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

DROP FUNCTION delete_consent(text);

create or replace function delete_consent(id text)
    returns TABLE
            (
                id                         text,
                state                      consent_status,
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
WHERE consents.id = $1
RETURNING id,
    state,
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

DROP FUNCTION get_consent(text);

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
                state                      consent_status,
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
       state,
       sm.name as chosen_sca_method,
       x_request_id,
       created_at
FROM consents
         JOIN sca_methods sm on consents.chosen_sca_method = sm.id
WHERE consents.id = $1;
$$;

DROP FUNCTION get_consent_by_x_request_id(uuid);

create or replace function get_consent_by_x_request_id(x_request_id uuid)
    returns TABLE
            (
                id                         text,
                access                     text,
                recurring_indicator        boolean,
				last_action 			   timestamp without time zone,
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
                state                      consent_status,
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
       state,
       sm.name as chosen_sca_method,
       x_request_id,
       created_at
FROM consents
         JOIN sca_methods sm on consents.chosen_sca_method = sm.id
WHERE consents.x_request_id = $1;
$$;

DROP FUNCTION update_consent(text, integer, text, boolean, timestamp without time zone, timestamp without time zone, 
							 integer, integer, boolean, character varying, character varying, integer, text, text, 
							 character varying, character varying, character varying, uuid);

create or replace function update_consent(id text, state character varying, access text, recurring_indicator boolean,
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
                state                      consent_status,
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
SET state                      = $2::consent_status,
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
    state,
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

DROP FUNCTION update_consent_state(text, integer);

create or replace function update_consent_state(id text, state character varying)
    returns TABLE
            (
                id                         text,
                state                      consent_status,
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
SET state        = $2::consent_status,
    last_updated = now()
WHERE consents.id = $1
RETURNING id,
    state,
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

DROP TABLE consent_states