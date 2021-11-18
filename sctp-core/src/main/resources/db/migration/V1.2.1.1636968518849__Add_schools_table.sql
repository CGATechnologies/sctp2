CREATE TABLE IF NOT EXISTS schools (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name varchar(250) NOT NULL,
	code bigint ,
	education_level bigint,
	education_zone bigint,
	created_at timestamp NOT NULL,
    modified_at timestamp
) comment 'This table keeps schools';

CREATE TABLE IF NOT EXISTS education_zone (
	id bigint PRIMARY KEY  AUTO_INCREMENT,
	name varchar(250) NOT NULL,
	district bigint,
	education_zone bigint,
	created_at timestamp NOT NULL,
    modified_at timestamp
) comment 'This table keeps education zones';

