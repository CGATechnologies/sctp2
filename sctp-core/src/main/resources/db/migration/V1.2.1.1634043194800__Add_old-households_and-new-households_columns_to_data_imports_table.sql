--
-- Add columns to data_imports table to statically keep track of new and returning households
--
DROP VIEW IF EXISTS data_imports_v;

ALTER TABLE data_imports
    ADD COLUMN old_households bigint after households;

ALTER TABLE data_imports
    ADD COLUMN new_households bigint after old_households;

-- Initialize counters
UPDATE data_imports SET new_households = 0, old_households = 0;

CREATE VIEW data_imports_v AS
SELECT
    di.*
    ,concat_ws(' ', u.first_name, u.last_name) AS imported_by
FROM data_imports di
JOIN users u ON u.id = di.importer_user_id;