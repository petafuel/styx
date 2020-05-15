alter table aspsps
    alter column aspsp_group_id drop not null;

alter table aspsps
    add documentation_url text;

create unique index aspsps_bic_production_url_id_uindex
    on aspsps (bic, production_url_id);
