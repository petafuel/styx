--
--Adjust get_bpd_data to only use active banks
--
DROP FUNCTION get_bpd_data(bics text[]);
CREATE OR REPLACE FUNCTION get_bpd_data(bics text[])
    RETURNS TABLE
            (
                bic              text,
                sca_methods      json,
                prestep_required boolean
            )
    SECURITY DEFINER
    LANGUAGE sql
as
$$
SELECT aspsps.bic,
       COALESCE(
               configs.config -> 'IO5' -> 'options',
               standards.config_template -> 'IO5' -> 'options'
           )                AS sca_methods,
       COALESCE(
               configs.config -> 'IO6' -> 'options' -> 'required',
               standards.config_template -> 'IO6' -> 'options' -> 'required'
           )::text::boolean AS prestep_required
FROM configs
         INNER JOIN standards ON standards.id = configs.standard_id
         INNER JOIN aspsps ON aspsps.config_id = configs.id
WHERE aspsps.bic = ANY ($1)
  AND aspsps.active = TRUE;
$$;