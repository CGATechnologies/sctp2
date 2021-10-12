DROP PROCEDURE IF EXISTS closeDataImportSession;

DELIMITER $$

CREATE PROCEDURE closeDataImportSession(IN _session_id bigint)
    COMMENT 'Runs final checks (duplicates, count) on the staged data and updates the import session. NB: Ignore the name'
BEGIN
UPDATE data_imports di
SET di.individuals = (SELECT count(id) total FROM ubr_csv_imports uci WHERE data_import_id = _session_id AND archived = false)
	,di.households = (SELECT count(DISTINCT form_number) total FROM ubr_csv_imports WHERE data_import_id = _session_id AND archived = false)
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