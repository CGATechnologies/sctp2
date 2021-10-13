--
-- Add group village head code and village name
--
-- Oct 12 2021 22:06
--

DROP VIEW IF EXISTS distinct_ubr_csv_import_household_records_v;

ALTER TABLE ubr_csv_imports
    ADD COLUMN village_name varchar(200)
   ,ADD COLUMN group_village_head_code bigint
;

-- Recreate the view
CREATE VIEW distinct_ubr_csv_import_household_records_v as
WITH unique_households AS (
	SELECT DISTINCT form_number, max(id) id
	FROM ubr_csv_imports uci
	WHERE validation_status = 'Valid' and archived = false
	GROUP BY form_number
)
SELECT uci.*
FROM ubr_csv_imports uci
JOIN unique_households uh ON uh.id = uci.id;