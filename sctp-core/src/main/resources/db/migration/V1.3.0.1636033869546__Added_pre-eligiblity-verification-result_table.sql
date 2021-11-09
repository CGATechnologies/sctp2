CREATE TABLE IF NOT EXISTS eligible_households (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	session_id bigint NOT NULL,
	household_id bigint NOT NULL,
	created_at timestamp NOT NULL,
	run_by bigint NOT NULL,
	
	CONSTRAINT eh_evs_id_fk FOREIGN key(session_id) REFERENCES eligibility_verification_sessions(id),
	CONSTRAINT eh_household_id_fk FOREIGN key(household_id) REFERENCES households(household_id),
	CONSTRAINT eh_user_id_fk FOREIGN key(run_by) REFERENCES users(id)
) comment 'This table keeps track of pre-eligibility verification results';