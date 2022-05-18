-- This view lists all households that have ever been targeted under community based targeting.
-- The result pairs all household columns with a targeting session.

DROP VIEW IF EXISTS targeted_households_view;

CREATE VIEW targeted_households_view
AS
select h.*
, tr.targeting_session
from households h
join targeting_results tr on tr.household_id = h.household_id
;