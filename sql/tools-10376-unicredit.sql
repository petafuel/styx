--
--Add the new STYX Option to UniCredit
--
DO
$$
    DECLARE
        unicredit_config_id int;
    BEGIN
        unicredit_config_id := (SELECT config_id FROM aspsps WHERE name = 'UniCredit HypoVereinsbank' LIMIT 1);

        -- insert a new Styx Option in the styx_configs of the Commerzbank configs
        UPDATE configs
        SET styx_config = (SELECT ((SELECT styx_config FROM configs WHERE id = unicredit_config_id)::jsonb || '{
          "STYX10": {
            "options": {
              "required": true
            },
            "description": "ASPSP requires PSU-ID-TYPE"
          }
        }'::jsonb)::jsonb),
         updated_at = now()
        WHERE id = unicredit_config_id;

    END
$$;
