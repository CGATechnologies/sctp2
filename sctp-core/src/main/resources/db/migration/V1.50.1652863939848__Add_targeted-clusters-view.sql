-- View to provide a fluent method of matching clusters and target sessions
-- Clusters are stored as a command separated list in clusters

DROP VIEW IF EXISTS targeted_clusters_view;

CREATE VIEW targeted_clusters_view
AS
SELECT l.id
	, l.code
	, l.name
	, l.location_type
	, ts.id session_id
FROM locations l
JOIN targeting_sessions ts ON FIND_IN_SET(l.code, ts.clusters) > 0