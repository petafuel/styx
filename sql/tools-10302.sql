--
--Add the new STYX Option to Sparda
--
DO
$$
    DECLARE
        sparda_config_id int;
    BEGIN
        sparda_config_id := (SELECT config_id FROM aspsps WHERE name ILIKE 'Sparda%' AND bic LIKE 'GENODE%' LIMIT 1);

        -- insert a new Styx Option in the styx_configs of the Sparda configs
        UPDATE configs
        SET styx_config = (SELECT ((SELECT styx_config FROM configs WHERE id = sparda_config_id)::jsonb || '{
          "STYX07": {
            "options": {
              "required": false
            },
            "description": "Starts payment poll task after payment initialization if required equals true"
          }
        }'::jsonb)::jsonb),
         updated_at = now()
        WHERE id = sparda_config_id;

        --2.1 enable redirect
        UPDATE configs SET config = jsonb_set(config::jsonb,'{IO5,options,redirect}'::text[], 'true'::jsonb) WHERE id = sparda_config_id;
-- 2.2 disable oauth
        UPDATE configs SET config = jsonb_set(config::jsonb,'{IO5,options,oauth}'::text[], 'false'::jsonb) WHERE id = sparda_config_id;
-- Statement 1.1 vor 19.05. nicht eingespielt
    END
$$;

-- 1. Update URLs
-- 1.1 update base url of the Sparda XS2A Interface LIVE
UPDATE urls SET url = 'https://api.sparda.de/xs2a/3.0.0', updated_at = now() WHERE url = 'https://api.sparda.de/xs2a/2.0.0';
-- 1.2 update base url of the Sparda XS2A Interface SANDBOX
UPDATE urls SET url = 'https://api.sparda.de.schulung.sparda.de:443/mock/3.0.0', updated_at = now() WHERE url = 'https://sandbox.sparda.de.schulung.sparda.de';
--2. update ImplementerOptions of Sparda

