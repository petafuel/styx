DO
$body$
    DECLARE
    	var_sparda_config_id integer;
    BEGIN
		--update base url of the Sparda XS2S Interface
		UPDATE urls SET url = 'https://api.sparda.de/xs2a/2.0.0', updated_at = now() WHERE url = 'https://api.sparda.de:443/xs2a';
	
		--update ImplementerOptions of Sparda
    	var_sparda_config_id := (SELECT config_id FROM aspsps WHERE name ILIKE 'Sparda%' AND bic LIKE 'GENODE%' LIMIT 1);
        --disable redirect
        UPDATE configs SET config = jsonb_set(config::jsonb,'{IO5,options,redirect}'::text[], 'false'::jsonb) WHERE id = var_sparda_config_id;
        --enable oauth
        UPDATE configs SET config = jsonb_set(config::jsonb,'{IO5,options,oauth}'::text[], 'true'::jsonb) WHERE id = var_sparda_config_id;
        --disable pre-step authentication
        UPDATE configs SET config = jsonb_set(config::jsonb,'{IO6,options,required}'::text[], 'false'::jsonb) WHERE id = var_sparda_config_id;
     	--set updated_at column
		UPDATE configs SET updated_at = now() WHERE id = var_sparda_config_id;
	END;
$body$
LANGUAGE 'plpgsql';