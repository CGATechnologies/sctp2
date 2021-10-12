--
-- Oct 12 2021 17:03
--
DROP PROCEDURE IF EXISTS closeDataImportSession;

DELIMITER $$

CREATE PROCEDURE closeDataImportSession(IN _session_id bigint)
    COMMENT 'Runs final checks (duplicates, count) on the staged data and updates the import session. NB: Ignore the name'
BEGIN
UPDATE data_imports di
SET di.individuals = (SELECT count(id) total FROM ubr_csv_imports uci WHERE data_import_id = _session_id AND archived = false)
	,di.households = (SELECT count(DISTINCT form_number) total FROM ubr_csv_imports WHERE data_import_id = _session_id AND archived = false)
	,di.new_households = (
	    SELECT count(distinct household_id)
	    FROM ubr_csv_imports
	    WHERE household_ml_code IS NULL AND data_import_id = _session_id and archived = FALSE
	)
	,di.old_households = (
        SELECT count(distinct household_id)
        FROM ubr_csv_imports
        WHERE household_ml_code IS NOT NULL AND data_import_id = _session_id and archived = FALSE
    )
	,di.batch_duplicates = (
		SELECT count(household_member_id)
		FROM ubr_csv_imports u
		WHERE data_import_id = _session_id AND archived = FALSE
		GROUP BY household_member_id
		HAVING count(household_member_id) > 1
	)
WHERE di.id = _session_id;
END$$

DELIMITER ;