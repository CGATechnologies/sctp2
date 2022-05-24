DROP PROCEDURE IF EXISTS getTargetingSessionsByLocation;

DELIMITER $func$

CREATE PROCEDURE getTargetingSessionsByLocation(
    IN _districtCode bigint,
    IN _taCode bigint,
    IN _clusterCode bigint,
    IN _page int,
    IN _pageSize int,
    IN _statusHint varchar(50),
    IN _appScm tinyint,
    IN _appDm tinyint
)
NOT DETERMINISTIC
COMMENT 'This function returns target_sessions_view filtered by the given locations'
BEGIN

SELECT ts.*
FROM targeting_sessions ts
WHERE status = _statusHint
AND ts.scm = _appScm
AND ts.dm  = _appDm
AND ts.district_code = _districtCode
AND (_taCode IS NULL OR ta_code = _taCode)
AND (_clusterCode IS NULL OR (SELECT EXISTS(SELECT 1 FROM targeted_clusters_view tcv WHERE tcv.session_id = ts.id AND tcv.code = _clusterCode)))
ORDER BY id ASC LIMIT _page, _pageSize
;

END $func$

DELIMITER ;