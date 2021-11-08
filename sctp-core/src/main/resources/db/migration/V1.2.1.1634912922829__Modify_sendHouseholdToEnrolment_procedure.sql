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
			(session_id, household_id)
			SELECT (SELECT LAST_INSERT_ID ()) AS session_id ,household_id from households h join
			targeting_sessions ts WHERE ts.id = targeting_session_id;

		END IF;

	END IF;
END $$

DELIMITER ;