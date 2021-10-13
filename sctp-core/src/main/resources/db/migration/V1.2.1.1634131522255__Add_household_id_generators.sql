--
-- Oct 13 2021 15:26
--

CREATE TABLE IF NOT EXISTS id_counters(
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name varchar(50) NOT NULL,
	counter bigint NOT NULL,
	CONSTRAINT UNIQUE unique_name (name)
) comment 'Keep track of various IDs. Values here must be seeded before dependent functions are called. ';

-- Initialize the counters
INSERT INTO id_counters(name, counter)
VALUES ('household_id', 300000);

-- Create function to increment and return the ID counter
DROP FUNCTION IF EXISTS getAndIncrementHouseholdId;

DELIMITER $$
CREATE FUNCTION getAndIncrementHouseholdId(_name varchar(50))
RETURNS BIGINT
comment 'Increments the household ID counter and returns the next ID to be assigned. If an error occurs between calling this procedure and the returned value being used, the value will be lost and it will create a gap in the sequence'
BEGIN
	DECLARE nextId bigint;
	SET nextId = (SELECT counter FROM id_counters WHERE name = _name);
	IF nextId IS NOT NULL THEN
		UPDATE id_counters SET counter = counter + 1 WHERE name = _name;
	END IF;
	RETURN COALESCE(nextId, 0);
END
$$

DELIMITER ;

--
-- Create a trigger to assign household IDs
--
DROP TRIGGER IF EXISTS generateHouseholdMlCode;

DELIMITER $$
$$
CREATE TRIGGER generateHouseholdMlCode
BEFORE INSERT ON households
FOR EACH ROW
BEGIN
	IF NEW.ml_code IS NULL THEN
		SET NEW.ml_code = (SELECT getAndIncrementHouseholdId('household_id'));
	END IF;
END$$
DELIMITER ;