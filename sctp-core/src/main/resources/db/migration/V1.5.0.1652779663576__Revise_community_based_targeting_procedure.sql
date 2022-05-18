DROP PROCEDURE IF EXISTS runCommunityBasedTargetingRanking;

CREATE PROCEDURE runCommunityBasedTargetingRanking(IN _session_id bigint)
    COMMENT 'This procedure will rank households based on the pmt_score from UBR.'
BEGIN
	INSERT INTO targeting_results (targeting_session,household_id,ranking,pmt_score,status,created_at,updated_at)
	SELECT
		_session_id AS targeting_session
		, h.household_id
		, ROW_NUMBER() OVER(ORDER BY h.pmt_score ASC) AS ranking
		, h.pmt_score
		, 'PreEligible' AS status
		, CURRENT_TIMESTAMP() created_at 
		, CURRENT_TIMESTAMP() updated_at
	FROM targeting_sessions ts
	JOIN households h ON h.location_code = ts.district_code
	WHERE FIND_IN_SET(concat("", h.cluster_code), ts.clusters) AND ts.id  = _session_id
	ORDER BY h.pmt_score ASC;
END;