DROP VIEW IF EXISTS targeted_household_summary;

CREATE VIEW targeted_household_summary
AS
SELECT tr.targeting_session
	, h.household_id
	, h.ml_code
	, h.ubr_code AS form_number
	, (SELECT count(id) FROM individuals i WHERE i.household_id = h.household_id) AS members
	, l.name AS district
	, l.code AS district_code
	, l2.name AS ta
	, l2.code AS ta_code
	, l3.name AS cluster
	, l3.code AS cluster_code
	, l4.name AS "zone"
	, l4.code AS zone_code
	, l6.name AS village
	, l6.code AS village_code
	, h.group_village_head_name AS village_head
	, tr.status
	, tr.ranking
	, tr.created_at
	, CONCAT(i2.first_name, ' ', i2.last_name) AS household_head
	, (SELECT JSON_ARRAYAGG(
		JSON_OBJECT(
			'id', id
		  , 'created_at', created_at
		  , 'deleted_at', deleted_at
		  , 'modified_at', modified_at
		  , 'first_name', first_name
		  , 'last_name', last_name
		  , 'date_of_birth', date_of_birth
		  , 'gender', gender
		  , 'relationship', relationship_to_head
		  , 'household_code', household_code
		  , 'household_id', household_id
		  , 'individual_id', individual_id
		  , 'id_issue_date', id_issue_date
		  , 'id_expiry_date', id_expiry_date
		  , 'phone_number', phone_number
		  , 'education_level', highest_education_level
		  , 'grade_level', grade_level
		  , 'school_name', school_name
		  , 'disability', disability
		  , 'orphan_status', orphan_status
		  , 'fit_for_work', fit_for_work
		  , 'chronic_illness', chronic_illness
		  , 'status', status
		  , 'marital_status', marital_status
		))
		FROM individuals WHERE household_id = tr.household_id
	) AS member_details
	FROM targeting_results tr
	LEFT JOIN households h ON h.household_id = tr.household_id
	LEFT JOIN locations l ON l.code = h.location_code
	LEFT JOIN locations l2 ON l2.code = h.ta_code
	LEFT JOIN locations l3 ON l3.code = h.cluster_code
	LEFT JOIN locations l4 ON l4.code = h.zone_code
	LEFT JOIN locations l5 ON l5.code = h.group_village_head_code
	LEFT JOIN locations l6 ON l6.code = h.village_code
	LEFT JOIN individuals i2 ON i2.household_id = h.household_id AND i2.relationship_to_head = 1
;
