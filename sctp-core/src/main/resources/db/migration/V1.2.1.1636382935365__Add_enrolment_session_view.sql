DROP VIEW IF EXISTS enrollment_session_v;

CREATE VIEW enrollment_session_v
AS SELECT
es.id,
es.created_at,
tsv.closer_name AS done_by,
tsv.ta_name,
tsv.district_name,
tsv.program_name,
(SELECT COUNT(id) FROM household_enrollment WHERE session_id = es.id) AS household_count
FROM enrollment_sessions AS es
LEFT JOIN target_sessions_view tsv ON es.target_session_id = tsv.id;