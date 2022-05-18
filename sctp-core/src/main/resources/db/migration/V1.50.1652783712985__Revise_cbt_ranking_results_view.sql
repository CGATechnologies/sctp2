-- cbt_ranking_results view provides summarized data for CBT Screens
DROP VIEW IF EXISTS cbt_ranking_results_v;

CREATE VIEW cbt_ranking_results_v
AS
SELECT
	h.household_id household_id
	,h.ubr_code form_number
	,l.name district_name
	,l2.name ta_name
	,l3.name zone_name
	,l4.name cluster_name
	,l5.name village_name
	, CONCAT(i.first_name, ' ', i.last_name) household_head
	,tr.ranking  "rank"
	,tr.targeting_session cbt_session_id
	,tr.status status
	,tr.pmt_score pmt_score
	,(SELECT count(id) FROM individuals i2 WHERE i2.household_id = h.household_id) AS member_count
	,h.ml_code ml_code
	,h.group_village_head_name village_head_name
FROM targeting_results tr
JOIN households h ON h.household_id = tr.household_id
LEFT JOIN locations l ON l.code = h.location_code
LEFT JOIN locations l2 ON l2.code = h.ta_code
LEFT JOIN locations l3 ON l3.code = h.zone_code
LEFT JOIN locations l4 ON l4.code = h.cluster_code
LEFT JOIN locations l5 ON l5.code = h.village_code
LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
;
