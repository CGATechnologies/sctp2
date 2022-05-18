DROP PROCEDURE IF EXISTS runCommunityBasedTargetingRankingOnEligibleHouseholds;

DELIMITER $$
CREATE PROCEDURE runCommunityBasedTargetingRankingOnEligibleHouseholds(IN _cbt_session_id bigint, IN _pev_session_id bigint)
    COMMENT 'This procedure will rank households based on the pmt_score from UBR. Unlike `runCommunityBasedTargetingRanking` which uses the entire population set, the source of the household list will come from the pre-eligibilty verification session, whose id is passed here.'
BEGIN
	INSERT INTO targeting_results (targeting_session,household_id,ranking,pmt_score,status,created_at,updated_at)
	SELECT _cbt_session_id AS targeting_session
		, h.household_id
		, ROW_NUMBER() OVER(ORDER BY h.pmt_score ASC) AS ranking
		, h.pmt_score
		, 'PreEligible' AS status
		, CURRENT_TIMESTAMP() created_at
		, CURRENT_TIMESTAMP() updated_at
	FROM eligible_households eh
	JOIN households h ON h.household_id = eh.household_id
	WHERE eh.session_id = _pev_session_id
	ORDER BY h.pmt_score ASC;
END $$

DELIMITER ;