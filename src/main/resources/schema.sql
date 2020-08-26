-- use for production system

CREATE TABLE post (
    id bigint NOT NULL CONSTRAINT post_pkey PRIMARY KEY,
    secret integer NOT NULL,
    create_date timestamp NOT NULL,
    last_updated timestamp,
    last_answered timestamp,
    nick character varying(64) NOT NULL,
    email character varying(64) NOT NULL,
    subject character varying(256) NOT NULL,
    content text
);
CREATE INDEX post_last_anwered_i ON post (last_answered DESC NULLS LAST);

CREATE SEQUENCE post_s INCREMENT 20 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;

CREATE TABLE answer (
    id bigint NOT NULL CONSTRAINT answer_pkey PRIMARY KEY,
    secret integer NOT NULL,
    post_id bigint NOT NULL,
    create_date timestamp NOT NULL,
    last_updated timestamp,
    last_answered timestamp,
    nick character varying(64) NOT NULL,
    email character varying(64) NOT NULL,
    CONSTRAINT answer_post_id_fk FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE INDEX answer_create_date_i ON answer USING btree (create_date ASC);
CREATE INDEX answer_post_id_i ON answer USING hash (post_id);

CREATE SEQUENCE answer_s INCREMENT 100 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1;
