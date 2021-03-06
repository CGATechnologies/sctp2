CREATE TABLE `transfer_agencies_assignments` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`enrollment_session_id` BIGINT NOT NULL COMMENT 'The Enrollment under which the transfer agency will be assigned',
	`program_id` BIGINT NOT NULL COMMENT 'Program ID of the current assignment (a Transfer agency can be assigned to same location in different programs)',
	`location_id` BIGINT NOT NULL COMMENT 'Location ID of the assignment, typically a Village Cluster or Zone location, but can also be a District location',
	`transfer_agency_id` BIGINT NOT NULL COMMENT 'ID of the Transfer Agency Assigned to the given location',
	`transfer_method` ENUM('Manual','EPayment') NOT NULL COMMENT 'Whether the Transfer Agency will perform manual or e-payment transfers in this location' COLLATE 'utf8mb4_general_ci',
	`assigned_by` BIGINT NULL DEFAULT NULL COMMENT 'User who assigned the transfer agency to the location',
	`status_code` INT(10) NULL DEFAULT NULL COMMENT 'Status code of the assignment, which has to be reviewed first',
	`reviewed_by` BIGINT NULL DEFAULT NULL COMMENT 'User who reviewed (an approved) the assignment, must != assigned_by',
	`created_at` TIMESTAMP NOT NULL,
	`modified_at` TIMESTAMP NOT NULL,
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `fk_es_taa_id` (`enrollment_session_id`) USING BTREE,
	INDEX `fk_pg_taa_id` (`program_id`) USING BTREE,
	INDEX `fk_lc_taa_id` (`location_id`) USING BTREE,
	INDEX `fk_ta_taa_id` (`transfer_agency_id`) USING BTREE,
	INDEX `fk_sc_taa_id` (`status_code`) USING BTREE,
	INDEX `fk_ab_taa_id` (`assigned_by`) USING BTREE,
	INDEX `fk_rb_taa_id` (`reviewed_by`) USING BTREE,
	CONSTRAINT `fk_ab_taa_id` FOREIGN KEY (`assigned_by`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `fk_es_taa_id` FOREIGN KEY (`enrollment_session_id`) REFERENCES `enrollment_sessions` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `fk_lc_taa_id` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `fk_pg_taa_id` FOREIGN KEY (`program_id`) REFERENCES `programs` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `fk_rb_taa_id` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `fk_sc_taa_id` FOREIGN KEY (`status_code`) REFERENCES `status_codes` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION,
	CONSTRAINT `fk_ta_taa_id` FOREIGN KEY (`transfer_agency_id`) REFERENCES `transfer_agencies` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION
);