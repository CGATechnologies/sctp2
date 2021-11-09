DROP PROCEDURE IF EXISTS calculateVerificationSessionHouseholdCount;

DELIMITER $$

CREATE PROCEDURE calculateVerificationSessionHouseholdCount(IN _session_id_ BIGINT)
BEGIN
UPDATE eligibility_verification_sessions 
SET households = (
	SELECT count(id)
	FROM  eligible_households eh 
	WHERE eh.session_id = _session_id_ 
)
WHERE id = _session_id_;
END$$