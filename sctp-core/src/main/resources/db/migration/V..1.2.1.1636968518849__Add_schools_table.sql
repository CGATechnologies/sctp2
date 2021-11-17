CREATE TABLE IF NOT EXISTS schools (
	id bigint PRIMARY KEY ,
	name varchar(250) NOT NULL,
	code bigint NOT NULL,
	district bigint,
	education_level bigint,
	ta bigint,
	education_zone bigint,
	created_at timestamp NOT NULL,
    modified_at timestamp,
) comment 'This table keeps schools';

