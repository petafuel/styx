
-- drop old function
drop function if exists create_token(client_master_token character varying, access_token character varying, service character varying, expires_in integer);