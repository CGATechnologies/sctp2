DROP PROCEDURE IF EXISTS getFilterValuesForCriterion;

DELIMITER //
CREATE PROCEDURE getFilterValuesForCriterion(IN _criterion_id bigint)
LANGUAGE SQL
begin
	SELECT cf.id
		, ft.table_name tableName
		, ft.column_name columnName
		, cf.conjunction
		, cf.filter_value filterValue
		, ft.field_type fieldType
		, ft.label 
	FROM criteria_filters cf
	JOIN filter_templates ft ON ft.id = cf.template_id 
	WHERE cf.criterion_id = _criterion_id
	ORDER BY cf.id ASC;
END//

DELIMITER ;
