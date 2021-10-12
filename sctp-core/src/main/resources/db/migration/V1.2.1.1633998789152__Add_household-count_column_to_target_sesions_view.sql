DROP VIEW IF EXISTS target_sessions_view;

CREATE VIEW target_sessions_view AS
SELECT
    ts.id AS id,
    ts.created_at AS created_at,
    ts.closed_at AS closed_at,
    ts.closed_by AS closed_by,
    ts.created_by AS created_by,
    ts.district_code AS district_code,
    ts.program_id AS program_id,
    ts.ta_code AS ta_code,
    ts.status AS status,
    ts.clusters AS clusters,
    p.name AS program_name,
    d.name AS district_name,
    t.name AS ta_name,
    concat(u.first_name, ' ', u.last_name) AS creator_name,
    concat(u2.first_name, ' ', u2.last_name) AS closer_name,
    (SELECT count(household_id) FROM households WHERE cbt_session_id = ts.id) AS household_count
FROM
    targeting_sessions ts
LEFT JOIN programs p ON p.id = ts.program_id
LEFT JOIN locations d ON d.code = ts.district_code
LEFT JOIN locations t ON t.code = ts.ta_code
LEFT JOIN users u ON u.id = ts.created_by
LEFT JOIN users u2 ON u2.id = ts.closed_by
ORDER BY ts.created_at DESC;