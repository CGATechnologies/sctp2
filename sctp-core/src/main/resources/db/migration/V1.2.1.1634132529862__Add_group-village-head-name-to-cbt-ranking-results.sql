--
-- Oct 13 2021 15:42
--
DROP PROCEDURE IF EXISTS getCbtRankingResults;

DELIMITER $$
CREATE PROCEDURE getCbtRankingResults(IN _session_id bigint, IN page int, IN page_size int)
    COMMENT 'Retrieves community based targeting results given a session id'
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
	,h.cbt_status status
	,h.last_cbt_ranking lastRanking
	,(SELECT count(id) FROM individuals i2 WHERE i2.household_id = h.household_id) AS memberCount
	,h.ml_code mlCode
	,h.group_village_head_name villageHeadName
FROM households h
LEFT JOIN locations l ON l.code = h.location_code
LEFT JOIN locations l2 ON l2.code = h.ta_code
LEFT JOIN locations l3 ON l3.code = h.zone_code
LEFT JOIN locations l4 ON l4.code = h.cluster_code
LEFT JOIN locations l5 ON l5.code = h.village_code
LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
WHERE cbt_session_id = _session_id
ORDER BY h.cbt_rank ASC LIMIT page, page_size;

END
$$
DELIMITER ;