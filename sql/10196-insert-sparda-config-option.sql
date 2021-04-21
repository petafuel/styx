--
--Add the new STYX Option to all Sparkasse Banks
--
DO
$$
    DECLARE
        sparkasse_config_id int;
    BEGIN
        sparkasse_config_id := (SELECT config_id FROM aspsps WHERE name iLIKE '%sparka%' LIMIT 1);

        -- insert a new Styx Option in the styx_configs of the Sparkasse configs
        UPDATE configs
        SET styx_config = (SELECT ((SELECT styx_config FROM configs WHERE id = sparkasse_config_id)::jsonb || '{
          "STYX06": {
            "options": {
              "required": true
            },
            "description": "Modifies the ReadTransactionRequest by adding the `Accept: application/xml` header. The list of transactions in the response from the bank is expected to be a `camt052` format"
          }
        }'::jsonb)::jsonb),
         updated_at = now()
        WHERE id = sparkasse_config_id;

    END
$$;