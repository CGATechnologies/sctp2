DROP VIEW IF EXISTS targeting_criteria_view;

CREATE VIEW targeting_criteria_view
AS
SELECT tc.*
	, (SELECT count(id) FROM criteria_filters cf WHERE cf.criterion_id = tc.id) AS filters
	, CONCAT(u.first_name,' ', u.last_name) creator_name
	, (SELECT COUNT(id) FROM eligibility_verification_sessions WHERE criterion_id = tc.id) AS verifications
FROM targeting_criteria tc
JOIN users u ON u.id = tc.created_by;
