-- Table: aspsp_groups

-- DROP TABLE aspsp_groups;

CREATE TABLE aspsp_groups
(
    id                serial                      NOT NULL,
    name              character varying(50)       NOT NULL,
    documentation_url character varying(255),
    updated_at        timestamp without time zone NOT NULL,
    created_at        timestamp without time zone NOT NULL,
    CONSTRAINT aspsp_group_id_seq PRIMARY KEY (id)
)
    WITH (
        OIDS= FALSE
    );

-- Index: aspsp_groups_id_uindex

-- DROP INDEX aspsp_groups_id_uindex;

CREATE UNIQUE INDEX aspsp_groups_id_uindex
    ON aspsp_groups
        USING btree
        (id);


-- Table: standards

-- DROP TABLE standards;

CREATE TABLE standards
(
    id              serial                      NOT NULL,
    name            character varying(50)       NOT NULL,
    version         character varying(10)       NOT NULL,
    config_template json,
    updated_at      timestamp without time zone NOT NULL,
    created_at      timestamp without time zone NOT NULL,
    CONSTRAINT standard_id_seq PRIMARY KEY (id)
)
    WITH (
        OIDS= FALSE
    );

-- Index: standards_id_uindex

-- DROP INDEX standards_id_uindex;

CREATE UNIQUE INDEX standards_id_uindex
    ON standards
        USING btree
        (id);

-- Table: configs

-- DROP TABLE configs;

CREATE TABLE configs
(
    id          serial                      NOT NULL,
    standard_id integer                     NOT NULL,
    config      json,
    updated_at  timestamp without time zone NOT NULL,
    created_at  timestamp without time zone NOT NULL,
    CONSTRAINT config_id_seq PRIMARY KEY (id),
    CONSTRAINT standard_id_fk FOREIGN KEY (standard_id)
        REFERENCES standards (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
    WITH (
        OIDS= FALSE
    );

-- Index: configs_id_uindex

-- DROP INDEX configs_id_uindex;

CREATE UNIQUE INDEX configs_id_uindex
    ON configs
        USING btree
        (id);


-- Table: urls

-- DROP TABLE urls;

CREATE TABLE urls
(
    id         serial                      NOT NULL,
    url        character varying(255),
    ais_url    character varying(255),
    pis_url    character varying(255),
    piis_url   character varying(255),
    updated_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone NOT NULL,
    CONSTRAINT url_id_seq PRIMARY KEY (id)
)
    WITH (
        OIDS= FALSE
    );

-- Index: urls_id_uindex

-- DROP INDEX urls_id_uindex;

CREATE UNIQUE INDEX urls_id_uindex
    ON urls
        USING btree
        (id);

-- Table: aspsps

-- DROP TABLE aspsps;

CREATE TABLE aspsps
(
    id                serial                      NOT NULL,
    aspsp_group_id    integer                     NOT NULL,
    config_id         integer                     NOT NULL,
    sandbox_url_id    integer                     NOT NULL,
    production_url_id integer                     NOT NULL,
    name              character varying(50)       NOT NULL,
    bic               character varying(11)       NOT NULL,
    active            boolean                     NOT NULL DEFAULT false,
    updated_at        timestamp without time zone NOT NULL,
    created_at        timestamp without time zone NOT NULL,
    CONSTRAINT aspsp_id_seq PRIMARY KEY (id),
    CONSTRAINT aspsp_group_id_fk FOREIGN KEY (aspsp_group_id)
        REFERENCES aspsp_groups (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT config_id_fk FOREIGN KEY (config_id)
        REFERENCES configs (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT production_url_id_fk FOREIGN KEY (production_url_id)
        REFERENCES urls (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT sandbox_url_id_fk FOREIGN KEY (sandbox_url_id)
        REFERENCES urls (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
    WITH (
        OIDS= FALSE
    );

-- Index: aspsps_id_uindex

-- DROP INDEX aspsps_id_uindex;

CREATE UNIQUE INDEX aspsps_id_uindex
    ON aspsps
        USING btree
        (id);
