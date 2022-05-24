DROP PROCEDURE IF EXISTS countTargetingSessionsByLocation;

DELIMITER $func$

CREATE PROCEDURE countTargetingSessionsByLocation(
    IN _districtCode bigint,
    IN _taCode bigint,
    IN _clusterCode bigint,
    IN _statusHint varchar(50),
    IN _appScm tinyint,
    IN _appDm tinyint
)
NOT DETERMINISTIC
COMMENT 'Counts the total records that would be returned by getTargetingSessionsByLocation. This is used for paging in the API.'
BEGIN

SELECT count(ts.id)  totalSessions
FROM target_sessions_view ts
WHERE status = _statusHint
AND ts.scm = _appScm
AND ts.dm  = _appDm
AND ts.district_code = _districtCode
AND (_taCode IS NULL OR ta_code = _taCode)
AND (_clusterCode IS NULL OR (SELECT EXISTS(SELECT 1 FROM targeted_clusters_view tcv WHERE tcv.session_id = ts.id AND tcv.code = _clusterCode)))
ORDER BY id ASC
;

END $func$

DELIMITER ;