DROP PROCEDURE IF EXISTS addHouseholdAlternateRecipient;

DELIMITER $$

CREATE PROCEDURE addHouseholdAlternateRecipient(IN householdId bigint,
IN mainRecipient bigint,
IN mainPhoto varchar(150),
IN altPhoto varchar(150),
IN firstName varchar(150),
IN lastName varchar(150),
IN nationalId varchar(150),
IN gender bigint,
IN dob date)
BEGIN
	INSERT INTO alternate_recipient
	(household_id, first_name, last_name, national_id, date_of_birth, gender)
	VALUES(householdId, firstName, lastName, nationalId, dob, gender);
	/* insert into household recipient details */
	INSERT INTO household_recipient
	(household_id, main_recipient, alt_recipient, main_photo, alt_photo, alt_other, created_at)
	VALUES(householdId, mainRecipient, 0, mainPhoto, altPhoto, (select last_insert_id()), CURRENT_TIMESTAMP());

END
$$
DELIMITER ;