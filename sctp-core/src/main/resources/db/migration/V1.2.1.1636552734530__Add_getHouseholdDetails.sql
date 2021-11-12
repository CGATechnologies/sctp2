DROP PROCEDURE IF EXISTS getHouseholdDetails;

DELIMITER $$
CREATE PROCEDURE getHouseholdDetails(IN _household_id bigint)
    COMMENT 'Retrieves enrolled households details'
BEGIN
SELECT
	h.household_id householdId
	,h.ubr_code formNumber
	,l.name districtName
	,l2.name taName
	,l3.name zoneName
	,l4.name clusterName
	,l5.name villageName
	,h.ml_code mlCode
    ,h.group_village_head_name villageHeadName
	, CONCAT(i.first_name, ' ', i.last_name) householdHead
	,(SELECT count(id) FROM individuals i2 WHERE i2.household_id = h.household_id) AS memberCount
	,(select count(id) from individuals i3 where i3.household_id = h.household_id and TIMESTAMPDIFF(YEAR, i3.date_of_birth, CURDATE()) >=6 and TIMESTAMPDIFF(YEAR, i3.date_of_birth, CURDATE()) <= 15 ) as totalChildren
	,(select count(id) from individuals i3 where i3.household_id = h.household_id and i3.highest_education_level = 2) as primaryChildren
	,(select count(id) from individuals i3 where i3.household_id = h.household_id and i3.highest_education_level = 3) as secondaryChildren
FROM households h
LEFT JOIN locations l ON l.code = h.location_code
LEFT JOIN locations l2 ON l2.code = h.ta_code
LEFT JOIN locations l3 ON l3.code = h.zone_code
LEFT JOIN locations l4 ON l4.code = h.cluster_code
LEFT JOIN locations l5 ON l5.code = h.village_code
LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
WHERE h.household_id = _household_id;

END
$$
DELIMITER ;