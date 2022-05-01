ALTER TABLE transfers DROP COLUMN `transfer_period_start_month`;
ALTER TABLE transfers DROP COLUMN `transfer_period_start_year`;
ALTER TABLE transfers DROP COLUMN `transfer_period_end`;
ALTER TABLE transfers DROP COLUMN `transfer_period_end_year`;

ALTER TABLE transfers ADD COLUMN `transfer_period_id` BIGINT(11) NOT NULL AFTER `transfer_session_id`;
