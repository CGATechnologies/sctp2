INSERT INTO targeting_results(targeting_session,household_id,ranking,pmt_score,status,created_at,updated_at)
SELECT
h.cbt_session_id targeting_session
, h.household_id 
, h.cbt_rank ranking
, h.pmt_score 
, CASE h.cbt_selection  
	WHEN 1 THEN "Eligible"
	WHEN 2 THEN "Ineligible"
	WHEN 3 THEN "Selected"
	WHEN 4 THEN "NonRecertified"
	WHEN 5 THEN "Enrolled"
	WHEN 6 THEN "PreEligible"
  END AS status 
, h.last_cbt_ranking created_at 
, h.last_cbt_ranking updated_at
FROM targeting_sessions ts
JOIN households h ON h.cbt_session_id = ts.id
;