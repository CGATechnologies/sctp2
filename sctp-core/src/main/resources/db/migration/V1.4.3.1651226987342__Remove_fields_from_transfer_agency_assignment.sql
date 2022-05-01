-- Remove enrollment session id from the assignments table
ALTER TABLE transfer_agencies_assignments DROP CONSTRAINT `fk_es_taa_id`;
ALTER TABLE transfer_agencies_assignments DROP COLUMN `enrollment_session_id`;
-- Remove the following period fields as there is now a dedicated TransferPeriod table
ALTER TABLE transfer_agencies_assignments DROP COLUMN `period_start_date`;
ALTER TABLE transfer_agencies_assignments DROP COLUMN `period_end_date`;