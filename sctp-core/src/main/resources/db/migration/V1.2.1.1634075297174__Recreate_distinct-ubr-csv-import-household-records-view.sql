DROP VIEW IF EXISTS distinct_ubr_csv_import_household_records_v;

-- Recreate the view
CREATE VIEW distinct_ubr_csv_import_household_records_v AS
WITH unique_households AS (
	SELECT DISTINCT form_number, MAX(id) id
	FROM ubr_csv_imports uci
	WHERE validation_status = 'Valid' AND archived = FALSE
	GROUP BY form_number
)
SELECT uci.*
FROM ubr_csv_imports uci
JOIN unique_households uh ON uh.id = uci.id;