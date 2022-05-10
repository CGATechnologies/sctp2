-- Adds closed field to transfer periods
ALTER TABLE `transfer_periods` ADD COLUMN closed INT(1) default '0' COMMENT "Indicates whether Transfer Periods is closed or not" AFTER opened_by;