DROP PROCEDURE IF EXISTS getDataImportDuplicates;

DELIMITER $$

CREATE PROCEDURE getDataImportDuplicates(IN _session_id bigint, IN page int, IN page_size int)
    COMMENT 'Returns an import sessions duplicates'
BEGIN
SELECT *
FROM ubr_csv_imports uci
JOIN (
	SELECT u.household_member_id
	FROM ubr_csv_imports u
	WHERE data_import_id = _session_id and archived = false
	GROUP BY household_member_id
	HAVING count(household_member_id) > 1
) AS t ON t.household_member_id = uci.household_member_id
WHERE uci .data_import_id  = _session_id ORDER BY uci.household_member_id LIMIT page, page_size ;
END$$

DELIMITER ;
