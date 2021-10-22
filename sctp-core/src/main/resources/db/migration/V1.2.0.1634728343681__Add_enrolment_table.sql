CREATE TABLE IF NOT EXISTS enrollment_sessions (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	created_by bigint,
	created_at timestamp NOT NULL,
	name varchar(100),
	target_session_id bigint,
	verification_session_id bigint,
	clusters varchar(100) comment 'Comma separated list of cluster codes'
) comment 'Household enrollment session created from community based targeting and verification';


/*
	Household enrolment list
*/
CREATE TABLE IF NOT EXISTS household_enrollment (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	session_id bigint NOT NULL,
	household_id bigint NOT NULL,
	CONSTRAINT es_id_fk FOREIGN KEY (session_id) REFERENCES enrollment_sessions(id),
	CONSTRAINT hh_id_fk FOREIGN KEY (household_id) REFERENCES households(household_id)
) comment 'This table keep a list of reference to households that were enrolled at a given point in time';
