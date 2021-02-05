--
--Add the new STYX Option to all Sparda Banks
--Disable STYX02 and STYX03 for all Sparda Banks
--
DO
$$
    DECLARE
        sparda_config_id int;

    BEGIN
        sparda_config_id := (SELECT config_id FROM aspsps WHERE name ILIKE 'Sparda%' AND bic ILIKE 'GENODE%' LIMIT 1);
UPDATE urls
SET url = 'https://api.sparda.de.schulung.sparda.de:443/mock/2.0.0', updated_at = now()
WHERE url = 'https://api-mock.sparda.de/mock';
        UPDATE configs
        SET styx_config = (SELECT ((SELECT styx_config FROM configs WHERE id = sparda_config_id)::jsonb || '{
          "STYX05": {
            "options": {
              "required": true
            },
            "description": "Processes the URL of the Authorization Endpoint and starts the OAuth-SCA"
          }
        }'::jsonb)::jsonb)
        WHERE id = sparda_config_id;


        UPDATE configs SET styx_config = jsonb_set(styx_config::jsonb,'{STYX02,options,required}'::text[], 'false'::jsonb) WHERE id = sparda_config_id;
        UPDATE configs SET styx_config = jsonb_set(styx_config::jsonb,'{STYX03,options,required}'::text[], 'false'::jsonb) WHERE id = sparda_config_id;
    END
$$;