--
--Add the new STYX Option to all sparda banks
--
DO
$$
    DECLARE
        sparda_config_id int;

    BEGIN
        sparda_config_id := (SELECT config_id FROM aspsps WHERE name ILIKE 'Sparda%' AND bic ILIKE 'GENODE%' LIMIT 1);

        UPDATE configs
        SET styx_config = (SELECT ((SELECT styx_config FROM configs WHERE id = sparda_config_id)::jsonb || '{
          "STYX04": {
            "options": {
              "required": true
            },
            "description": "Adds a X-BIC Header to the request before sending to the target ASPSP"
          }
        }'::jsonb)::jsonb)
        WHERE id = sparda_config_id;
    END
$$;