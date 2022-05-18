-- Only for reference

DROP VIEW IF EXISTS selection_status_v;

CREATE VIEW selection_status_v
AS
SELECT 1 AS code, "Eligible" AS status
UNION SELECT 2, "Ineligible"
UNION SELECT 3, "Selected"
UNION SELECT 4, "NonRecertified"
UNION SELECT 5, "Enrolled"
UNION SELECT 6, "PreEligible"
;