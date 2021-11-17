CREATE TABLE IF NOT EXISTS school_enrolled(
	id bigint PRIMARY KEY AUTO_INCREMENT,
	household_id bigint not null,
	individual_id bigint not null,
	education_level bigint  not null,
	grade bigint not null,
	school_id bigint not null,
	status int comment 'to check if the children are still active',
	CONSTRAINT hd_id_fk FOREIGN KEY (household_id) REFERENCES households(household_id),
	CONSTRAINT iv_id_fk FOREIGN KEY (individual_id) REFERENCES individuals(id)
) comment 'This table keeps track of children enrolled in a household';