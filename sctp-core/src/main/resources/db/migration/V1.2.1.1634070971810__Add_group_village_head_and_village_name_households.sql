--
-- Oct 12 2021 22:37
--

DROP VIEW IF EXISTS distinct_ubr_csv_import_household_records_v;

ALTER TABLE households
    ADD COLUMN village_name varchar(200)
   ,ADD COLUMN group_village_head_code bigint
   ,ADD COLUMN group_village_head_name bigint
;