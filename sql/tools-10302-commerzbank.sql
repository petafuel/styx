--
--Add the new STYX Option to Commerzbank
--
DO
$$
    DECLARE
        commerzbank_config_id int;
    BEGIN
        commerzbank_config_id := (SELECT config_id FROM aspsps WHERE name = 'Commerzbank' LIMIT 1);

        -- insert a new Styx Option in the styx_configs of the Commerzbank configs
        UPDATE configs
        SET styx_config = (SELECT ((SELECT styx_config FROM configs WHERE id = commerzbank_config_id)::jsonb || '{
          "STYX07": {
            "options": {
              "required": true
            },
            "description": "Starts payment poll task after payment initialization if required equals true"
          }
        }'::jsonb)::jsonb),
         updated_at = now()
        WHERE id = commerzbank_config_id;

    END
$$;
