CREATE TABLE IF NOT EXISTS targeting_results (
	id bigint NOT NULL auto_increment,
	targeting_session bigint NOT NULL comment 'Targeting session id',
	household_id bigint NOT NULL comment 'Household id',
	ranking int NOT NULL comment 'Ranking based PMT score. Can also be changed',
	status varchar(30) NOT NULL comment 'Status of the household at the current stage of the targeing: could be one of eligible, ineligible, selected.',
	created_at timestamp NOT NULL,
	updated_at timestamp,

	PRIMARY KEY (id),
	CONSTRAINT tr_household_id_fk FOREIGN KEY (household_id) REFERENCES households (household_id),
	CONSTRAINT tr_sessions_id_fk FOREIGN KEY (targeting_session) REFERENCES targeting_sessions (id),
	INDEX hhid_idx (household_id),
	INDEX ts_idx (targeting_session)
) COMMENT = 'Table to store community based targeting results. The community based targeting module allows performing second coommunity and district meetings.';