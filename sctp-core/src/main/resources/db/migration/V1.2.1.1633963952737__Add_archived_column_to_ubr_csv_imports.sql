--
-- Add 'archived' column to the ubr_csv_imports to be able to exclude records without completely deleting them.
--

-- Drop dependent views (Will be re-added) later
drop view if exists distinct_ubr_csv_import_household_records_v;

alter table ubr_csv_imports
    add column archived tinyint comment 'Soft-deletion marker. This record will be excluded from reviews and imports';

-- Initially set all records to false since previously before this, records were actually deleted
update ubr_csv_imports set archived = false where validation_status = 'Valid';
update ubr_csv_imports set archived = true where validation_status = 'Error';

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

