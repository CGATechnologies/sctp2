-- Transfer Periods table
-- Links a Transfers to Transfer agencies and sets the period of the transfer
-- Equivalent to term table in the old database
DROP TABLE IF EXISTS `transfer_periods`;
CREATE TABLE `transfer_periods` (
	`id` BIGINT(19) NOT NULL AUTO_INCREMENT,
	`program_id` BIGINT(19) NOT NULL,
	`transfer_session_id` BIGINT(19) NOT NULL,
	`district_id` BIGINT(19) NOT NULL,
	`ta_id` BIGINT(19) NOT NULL,
	`name` VARCHAR(100) NOT NULL COMMENT 'Human readable short name description of the period',
	`start_date` DATE NOT NULL COMMENT 'When the period starts',
	`end_date` DATE NOT NULL COMMENT 'When the period ends',
	`description` VARCHAR(100) NOT NULL  COMMENT 'description of the period',
	`bonus_primary_parameter_id` BIGINT(19) NULL DEFAULT NULL,
	`bonus_secondary_parameter_id` BIGINT(19) NULL DEFAULT NULL,
	`opened_by` BIGINT(19) NOT NULL COMMENT 'User who opened the transfer period',
	`created_at` TIMESTAMP NULL DEFAULT NULL,
	`updated_at` TIMESTAMP NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
);
