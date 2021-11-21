ALTER TABLE household_enrollment ADD status int;

DROP PROCEDURE IF EXISTS sendHouseholdToEnrolment;

DELIMITER $$
CREATE PROCEDURE sendHouseholdToEnrolment(IN targeting_session_id int,IN verification_session_id int,IN user_id int)
BEGIN

	IF(targeting_session_id > 0 OR verification_session_id > 0) THEN

		INSERT INTO enrollment_sessions
		(created_by, created_at,target_session_id, verification_session_id)
		VALUES(user_id, CURRENT_TIMESTAMP(),targeting_session_id, verification_session_id);

		IF(targeting_session_id > 0) THEN

			INSERT INTO household_enrollment
			(session_id, household_id,status)
			SELECT (SELECT LAST_INSERT_ID ()) AS session_id , household_id, cbt_status from households h join
			targeting_sessions ts WHERE ts.id = targeting_session_id;

		END IF;

	END IF;
END $$

DELIMITER ;

DROP PROCEDURE IF EXISTS getEnrolledHouseholds;

DELIMITER $$
CREATE PROCEDURE getEnrolledHouseholds(IN _session_id bigint, IN page int, IN page_size int)
    COMMENT 'Retrieves enrolled households by a session id'
BEGIN
SELECT
	h.household_id householdId
	,h.ubr_code formNumber
	,l.name districtName
	,l2.name taName
	,l3.name zoneName
	,l4.name clusterName
	,l5.name villageName
	, CONCAT(i.first_name, ' ', i.last_name) householdHead
	,h.cbt_rank "rank"
	,h.cbt_session_id  cbtSessionId
	,h.cbt_selection cbtSelection
	,h.cbt_pmt pmtScore
	,he.status status
	,h.last_cbt_ranking lastRanking
	,(SELECT count(id) FROM individuals i2 WHERE i2.household_id = h.household_id) AS memberCount
	,h.ml_code mlCode
    ,h.group_village_head_name villageHeadName
from household_enrollment he
left join households h on h.household_id = he.household_id
LEFT JOIN locations l ON l.code = h.location_code
LEFT JOIN locations l2 ON l2.code = h.ta_code
LEFT JOIN locations l3 ON l3.code = h.zone_code
LEFT JOIN locations l4 ON l4.code = h.cluster_code
LEFT JOIN locations l5 ON l5.code = h.village_code
LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
WHERE he.session_id = _session_id
ORDER BY h.cbt_rank ASC LIMIT page, page_size;

END
$$
DELIMITER ;