DROP PROCEDURE IF EXISTS getEligibleHouseholds;

DELIMITER $$

CREATE PROCEDURE getEligibleHouseholds(IN _session_id bigint)
COMMENT 'Gets a list of eligible households give a session id, under which the eligibilty verification was run'
BEGIN
	SELECT eh.session_id AS sessionId
	, h.household_id AS householdId
	, h.ubr_code AS formNumber
	, (SELECT count(id) FROM individuals i WHERE i.household_id = h.household_id) AS members
	, l.name AS district
	, l2.name AS ta
	, l3.name AS cluster
	, l4.name AS "zone"
	, h.group_village_head_name AS villageHead
	, CONCAT(i2.first_name, ' ', i2.last_name) AS householdHead
	FROM eligible_households eh
	LEFT JOIN households h ON h.household_id = eh.household_id
	LEFT JOIN locations l ON l.code = h.location_code
	LEFT JOIN locations l2 ON l2.code = h.ta_code
	LEFT JOIN locations l3 ON l3.code = h.cluster_code
	LEFT JOIN locations l4 ON l4.code = h.zone_code
	LEFT JOIN locations l5 ON l5.code = h.group_village_head_code
	LEFT JOIN individuals i2 ON i2.household_id = h.household_id AND i2.relationship_to_head = 1 /* 1 = Head */
	WHERE eh.session_id = _session_id;
END$$