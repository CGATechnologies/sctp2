DROP VIEW IF EXISTS targeting_criteria_view;

ALTER TABLE targeting_criteria
    ADD COLUMN compiled_query text comment 'Stores the compiled query that will be executed dynamically. The query is compiled each time a filter is added or removed.',
    ADD COLUMN compiled_at timestamp comment 'Timestamp of when the query was compiled.';

CREATE VIEW targeting_criteria_view
AS
SELECT tc.*
	, (SELECT count(id) FROM criteria_filters cf WHERE cf.criterion_id = tc.id) AS filters
	, CONCAT(u.first_name,' ', u.last_name) creator_name
FROM targeting_criteria tc
JOIN users u ON u.id = tc.created_by;
