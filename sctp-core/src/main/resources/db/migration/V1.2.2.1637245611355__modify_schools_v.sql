DROP VIEW IF EXISTS schools_v;

CREATE VIEW schools_v
AS
SELECT
s.id ,
s.name ,
s.code ,
s.education_level as educationLevel,
ez.name as educationZone,
l.name as districtName
FROM schools s
LEFT JOIN education_zone ez ON s.education_zone = ez.id
LEFT JOIN locations l ON l.id  = ez.district;