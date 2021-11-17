CREATE TABLE IF NOT EXISTS alternate_recipient(
	id bigint PRIMARY KEY AUTO_INCREMENT,
	household_id bigint not null,
	first_name varchar(120),
	last_name varchar(120),
	national_id varchar(120),
	date_of_birth date,
	gender int,
	CONSTRAINT hu_id_fk FOREIGN KEY (household_id) REFERENCES households(household_id)
) comment 'This table keeps alternate recipients individuals which are not part of sctp';