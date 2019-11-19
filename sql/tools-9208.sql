-- Table: aspsp_groups

-- DROP TABLE aspsp_groups;

CREATE TABLE aspsp_groups
(
  id integer NOT NULL DEFAULT nextval('aspsp_group_id_seq'::regclass),
  name character varying(50) NOT NULL,
  documentation_url character varying(255),
  updated_at timestamp without time zone NOT NULL,
  created_at timestamp without time zone NOT NULL,
  CONSTRAINT aspsp_group_id_seq PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE aspsp_groups
  OWNER TO prep_styx;
GRANT ALL ON TABLE aspsp_groups TO prep_styx;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsp_groups TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsp_groups TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsp_groups TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsp_groups TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsp_groups TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsp_groups TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsp_groups TO ***REMOVED***;

-- Index: aspsp_groups_id_uindex

-- DROP INDEX aspsp_groups_id_uindex;

CREATE UNIQUE INDEX aspsp_groups_id_uindex
  ON aspsp_groups
  USING btree
  (id);


-- Table: aspsps

-- DROP TABLE aspsps;

CREATE TABLE aspsps
(
  id integer NOT NULL DEFAULT nextval('aspsp_id_seq'::regclass),
  aspsp_group_id integer NOT NULL,
  config_id integer NOT NULL,
  sandbox_url_id integer NOT NULL,
  production_url_id integer NOT NULL,
  name character varying(50) NOT NULL,
  bic character varying(11) NOT NULL,
  active boolean NOT NULL DEFAULT false,
  updated_at timestamp without time zone NOT NULL,
  created_at timestamp without time zone NOT NULL,
  CONSTRAINT aspsp_id_seq PRIMARY KEY (id),
  CONSTRAINT aspsp_group_id_fk FOREIGN KEY (aspsp_group_id)
      REFERENCES aspsp_groups (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT config_id_fk FOREIGN KEY (config_id)
      REFERENCES configs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT production_url_id FOREIGN KEY (production_url_id)
      REFERENCES urls (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT sandbox_url_id_fk FOREIGN KEY (sandbox_url_id)
      REFERENCES urls (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE aspsps
  OWNER TO prep_styx;
GRANT ALL ON TABLE aspsps TO prep_styx;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsps TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsps TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsps TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsps TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsps TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsps TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE aspsps TO ***REMOVED***;

-- Index: aspsps_id_uindex

-- DROP INDEX aspsps_id_uindex;

CREATE UNIQUE INDEX aspsps_id_uindex
  ON aspsps
  USING btree
  (id);


-- Table: configs

-- DROP TABLE configs;

CREATE TABLE configs
(
  id integer NOT NULL DEFAULT nextval('config_id_seq'::regclass),
  standard_id integer NOT NULL,
  config json,
  updated_at timestamp without time zone NOT NULL,
  created_at timestamp without time zone NOT NULL,
  CONSTRAINT config_id_seq PRIMARY KEY (id),
  CONSTRAINT standard_id_fk FOREIGN KEY (standard_id)
      REFERENCES standards (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE configs
  OWNER TO prep_styx;
GRANT ALL ON TABLE configs TO prep_styx;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE configs TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE configs TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE configs TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE configs TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE configs TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE configs TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE configs TO ***REMOVED***;

-- Index: configs_id_uindex

-- DROP INDEX configs_id_uindex;

CREATE UNIQUE INDEX configs_id_uindex
  ON configs
  USING btree
  (id);


-- Table: standards

-- DROP TABLE standards;

CREATE TABLE standards
(
  id integer NOT NULL DEFAULT nextval('standard_id_seq'::regclass),
  name character varying(50) NOT NULL,
  version character varying(10) NOT NULL,
  config_template json,
  updated_at timestamp without time zone NOT NULL,
  created_at timestamp without time zone NOT NULL,
  CONSTRAINT standard_id_seq PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE standards
  OWNER TO prep_styx;
GRANT ALL ON TABLE standards TO prep_styx;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE standards TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE standards TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE standards TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE standards TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE standards TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE standards TO ***REMOVED***_member;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE standards TO ***REMOVED***_member;

-- Index: standards_id_uindex

-- DROP INDEX standards_id_uindex;

CREATE UNIQUE INDEX standards_id_uindex
  ON standards
  USING btree
  (id);


-- Table: urls

-- DROP TABLE urls;

CREATE TABLE urls
(
  id integer NOT NULL DEFAULT nextval('url_id_seq'::regclass),
  url character varying(255),
  ais_url character varying(255),
  pis_url character varying(255),
  piis_url character varying(255),
  updated_at timestamp without time zone NOT NULL,
  created_at timestamp without time zone NOT NULL,
  CONSTRAINT url_id_seq PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE urls
  OWNER TO prep_styx;
GRANT ALL ON TABLE urls TO prep_styx;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE urls TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE urls TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE urls TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE urls TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE urls TO ***REMOVED***;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE urls TO ***REMOVED***_member;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE urls TO ***REMOVED***_member;

-- Index: urls_id_uindex

-- DROP INDEX urls_id_uindex;

CREATE UNIQUE INDEX urls_id_uindex
  ON urls
  USING btree
  (id);