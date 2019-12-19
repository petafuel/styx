alter table aspsps
    alter column aspsp_group_id drop not null;

alter table aspsps
    add documentation_url text;