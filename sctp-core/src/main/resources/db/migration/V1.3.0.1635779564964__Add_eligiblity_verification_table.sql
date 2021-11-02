CREATE TABLE IF NOT EXISTS eligibility_verification_sessions (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	created_at timestamp NOT NULL,
	program_id bigint,
	households bigint comment 'Caches the number of households that match the criteria filters. This column should be updated when the criteria filters are applied.',
	user_id bigint NOT NULL,
	district_code bigint NOT NULL,
	ta_code bigint NOT NULL,
	clusters varchar(1024) COMMENT '',
	criterion_id bigint NOT NULL,
	status enum('Review', 'Closed') NOT NULL,

	CONSTRAINT evs_program_id_fk 	FOREIGN KEY (program_id) 	REFERENCES programs (id),
	CONSTRAINT evs_user_id_fk 	 	FOREIGN KEY (user_id) 		REFERENCES users (id),
	CONSTRAINT evs_district_code_fk FOREIGN KEY (district_code) REFERENCES locations (code),
	CONSTRAINT evs_ta_code_fk 		FOREIGN KEY (ta_code) 		REFERENCES locations (code),
	CONSTRAINT evs_criterion_id_fk 	FOREIGN KEY (criterion_id) 	REFERENCES targeting_criteria (id)
);

DROP VIEW IF EXISTS eligibility_verification_sessions_v;

CREATE VIEW eligibility_verification_sessions_v
AS
SELECT evs.*
	, CONCAT(u.first_name, ' ', u.last_name) AS created_by
	, p.name program_name
	, l.name AS district_name
	, l2.name AS ta_name
	, tc.name AS criterion_name
FROM eligibility_verification_sessions evs
JOIN users u ON u.id = evs.user_id
JOIN programs p ON p.id = evs.program_id
JOIN locations l ON l.code = evs.district_code
JOIN locations l2 ON l2.code = evs.ta_code
JOIN targeting_criteria tc ON tc.id = evs.criterion_id;