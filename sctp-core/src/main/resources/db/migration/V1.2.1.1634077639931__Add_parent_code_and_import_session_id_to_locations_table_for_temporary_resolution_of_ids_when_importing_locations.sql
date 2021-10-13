DROP VIEW IF EXISTS location_by_codes_v;
DROP VIEW IF EXISTS locations_info_v;
DROP VIEW IF EXISTS active_locations;

ALTER TABLE locations
    ADD COLUMN tmp_parent_code bigint
    COMMENT 'This column is used to resolve parent_id links during the merging of data from ubr_csv_imports to locations'
   ,ADD COLUMN tmp_import_id bigint
   COMMENT 'Used to identify which rows whose parent_id values are to be resolved. This is per import session.'
;

-- Recreate views
CREATE VIEW active_locations
    AS SELECT * FROM locations WHERE active = TRUE
;

CREATE VIEW locations_info_v
AS
SELECT
    l.id
     ,l.code
     ,l.name
     ,l.location_type as locationType
     ,t.description as locationDescription
     ,l.latitude
     ,l.longitude
     ,l.active
     ,l.parent_id as parentId
FROM locations l
LEFT JOIN terminologies t ON l.location_type = t.name
;

CREATE VIEW location_by_codes_v
AS
    SELECT child.*, parent.code parentCode
    FROM active_locations parent
    JOIN active_locations child ON child.parent_id = parent.id
;
