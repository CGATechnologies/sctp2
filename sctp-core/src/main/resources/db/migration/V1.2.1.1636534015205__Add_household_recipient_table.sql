CREATE TABLE IF NOT EXISTS household_recipient (
	household_id bigint PRIMARY KEY ,
	main_recipient bigint NOT NULL,
	alt_recipient bigint NOT NULL,
	main_photo varchar(250),
	alt_photo varchar(250),
	alt_other bigint,
	created_at timestamp NOT NULL,
    modified_at timestamp,
	CONSTRAINT ho_id_fk FOREIGN KEY (household_id) REFERENCES households(household_id)
) comment 'This table keeps household recipient details';
