-- This migration fixes a mistake where the following column was added to the wrong table.

DROP VIEW IF EXISTS target_sessions_view;

alter table targeting_sessions
    add column pev_session bigint
        comment 'if this session was automatically created from a pre-eligibiligy verification run, the id will be stored here'
        after id
;

CREATE VIEW target_sessions_view
AS
SELECT ts.*
	, p.name program_name
	, d.name district_name
	, t.name ta_name
	, CONCAT(u.first_name, ' ', u.last_name) creator_name
	, CONCAT(u2.first_name, ' ', u2.last_name) closer_name
	, (SELECT count(id) FROM targeting_results tr WHERE tr.targeting_session = ts.id) AS household_count
FROM targeting_sessions ts
LEFT JOIN programs p ON p.id = ts.program_id
LEFT JOIN locations d ON d.code = ts.district_code
LEFT JOIN locations t ON t.code = ts.ta_code
LEFT JOIN users u ON u.id = ts.created_by
LEFT JOIN users u2 ON u2.id = ts.closed_by
ORDER BY created_at DESC ;

-- Drop table
alter table targeting_results drop column pev_session;