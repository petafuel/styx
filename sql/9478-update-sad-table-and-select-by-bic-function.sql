alter table configs
    add styx_config json;

alter table public.standards
    add styx_config json;

drop function get_bank_by_bic(varchar);

create function get_bank_by_bic(input_bic character varying)
    returns TABLE
            (
                id                             integer,
                name                           character varying,
                bic                            character varying,
                active                         boolean,
                updated_at                     timestamp without time zone,
                created_at                     timestamp without time zone,
                documentation_url              text,
                aspsp_groups_id                integer,
                aspsp_groups_name              character varying,
                aspsp_groups_documentation_url character varying,
                aspsp_groups_updated_at        timestamp without time zone,
                aspsp_groups_created_at        timestamp without time zone,
                production_url_id              integer,
                production_url                 character varying,
                production_ais_url             character varying,
                production_pis_url             character varying,
                production_piis_url            character varying,
                production_url_updated_at      timestamp without time zone,
                production_url_created_at      timestamp without time zone,
                sandbox_url_id                 integer,
                sandbox_url                    character varying,
                sandbox_ais_url                character varying,
                sandbox_pis_url                character varying,
                sandbox_piis_url               character varying,
                sandbox_url_updated_at         timestamp without time zone,
                sandbox_url_created_at         timestamp without time zone,
                configs_id                     integer,
                config                         json,
                styx_config                    json,
                configs_standard_id            integer,
                configs_updated_at             timestamp without time zone,
                configs_created_at             timestamp without time zone,
                standards_id                   integer,
                standards_name                 character varying,
                standards_version              character varying,
                standards_config_template      json,
                standards_styx_config_template json,
                standards_updated_at           timestamp without time zone,
                standards_created_at           timestamp without time zone
            )
    security definer
    language sql
as
$$
SELECT aspsps.id,
       aspsps.name,
       aspsps.bic,
       aspsps.active,
       aspsps.updated_at,
       aspsps.created_at,
       aspsps.documentation_url,
       --aspsp_groups table
       aspsp_groups.id                as aspsp_groups_id,
       aspsp_groups.name              as aspsp_groups_name,
       aspsp_groups.documentation_url as aspsp_groups_documentation_url,
       aspsp_groups.updated_at        as aspsp_groups_updated_at,
       aspsp_groups.created_at        as aspsp_groups_created_at,
       --production url data
       urls_production.id             as production_url_id,
       urls_production.url            as production_url,
       urls_production.ais_url        as production_ais_url,
       urls_production.pis_url        as production_pis_url,
       urls_production.piis_url       as production_piis_url,
       urls_production.updated_at     as production_url_updated_at,
       urls_production.created_at     as production_url_created_at,
       --sandbox url data
       urls_sandbox.id                as sandbox_url_id,
       urls_sandbox.url               as sandbox_url,
       urls_sandbox.ais_url           as sandbox_ais_url,
       urls_sandbox.pis_url           as sandbox_pis_url,
       urls_sandbox.piis_url          as sandbox_piis_url,
       urls_sandbox.updated_at        as sandbox_url_updated_at,
       urls_sandbox.created_at        as sandbox_url_created_at,
       --configs data
       configs.id                     as configs_id,
       configs.config                 as configs_config,
       configs.styx_config            as configs_styx_config,
       configs.standard_id            as configs_standard_id,
       configs.updated_at             as configs_updated_at,
       configs.created_at             as configs_created_at,
       --standard data
       standards.id                   as standards_id,
       standards.name                 as standards_name,
       standards.version              as standards_version,
       standards.config_template      as standards_config_template,
       standards.styx_config_template as standards_styx_config_template,
       standards.updated_at           as standards_updated_at,
       standards.created_at           as standards_created_at
from aspsps
         left join aspsp_groups on aspsps.aspsp_group_id = aspsp_groups.id
         join urls urls_production on aspsps.production_url_id = urls_production.id
         join urls urls_sandbox on aspsps.sandbox_url_id = urls_sandbox.id
         join configs on aspsps.config_id = configs.id
         join standards on configs.standard_id = standards.id
where bic = input_bic
$$;
