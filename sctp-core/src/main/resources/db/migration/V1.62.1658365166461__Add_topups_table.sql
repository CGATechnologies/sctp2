-- Creates transfers_topups table which defines top ups for transfers in a specific program
CREATE TABLE `transfer_topups` (
	`id` BIGINT(19) PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
	`name` VARCHAR(90) NULL DEFAULT NULL COMMENT 'Name of topup' COLLATE 'utf8_unicode_ci',
	`funder_id` BIGINT(19) NOT NULL COMMENT 'Funding institution for this topup',
	`program_id` BIGINT(19) NOT NULL COMMENT 'Program for this topup',
	`location_id` INT(10) NOT NULL COMMENT 'FOREIGN KEY Table geolocation Field geo_id, District',
	`location_type` INT(10) NOT NULL COMMENT 'FOREIGN KEY Table item Field ite_id, Level',
    `is_discounted_from_funds` TINYINT(1) NULL DEFAULT NULL COMMENT 'The top-up arrears are discounted from the SCTP funds to be requested',
	`is_categorical` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Whether topup is categorical or not',
	`is_active`  TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Active or not 1 = yes, 0 = no',
	`is_executed` TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Executed or not 1 = yes, 0 = no',
	`topup_type` INT(3) NOT NULL DEFAULT '0' COMMENT 'Topup type, See TopupType enumeration',
	`household_status` INT(3) NOT NULL DEFAULT '0' COMMENT 'Topup Household status, see enumration',
	`percentage` DOUBLE COMMENT 'For percentage based topups, the percentage to use for calculation',
	`categorical_targeting_criteria_id` BIGINT(19) NULL COMMENT 'For categorical topups, the criteria to use.',
	`amount` BIGINT(19) NULL DEFAULT NULL COMMENT 'Amount to topup',
	`amount_projected` BIGINT(19) NULL DEFAULT NULL COMMENT 'Amount projected',
	`amount_executed` BIGINT(19) NULL DEFAULT NULL COMMENT 'Amount executed i.e. disbursed',
	`created_by` BIGINT(19) NOT NULL COMMENT 'User who created the topup',
	`updated_by` BIGINT(19) NOT NULL COMMENT 'User who updated the topup',
    `created_at` timestamp not null,
    `updated_at` timestamp not NULL
);
