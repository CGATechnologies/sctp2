--
-- Individuals information
--

CREATE TABLE IF NOT EXISTS `individuals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `date_of_birth` date NOT NULL,
  `is_estimated_dob` tinyint(1) NOT NULL,
  `location_code` varchar(50) NOT NULL,
  `deleted` tinyint not null,
  `status` int NOT NULL COMMENT 'Mortality status: Alive | Deceased',
  `gender` int NOT NULL COMMENT '1: MALE, 2: FEMALE',
  `marital_status` int NOT NULL COMMENT 'SINGLE, MARRIED, DIVORCED',
  `individual_id` varchar(50) NOT NULL COMMENT 'Unique individual ID. Additional IDs can be added in the other table',
  `id_issue_date` date,
  `id_expiry_date` date,
  `created_at` timestamp NOT NULL,
  `modified_at` timestamp,
  `deleted_at` timestamp,
  `phone_number` varchar(50) DEFAULT NULL,
  `disability` int NOT NULL,
  `chronic_illness` int NOT NULL,
  `fit_for_work` tinyint NOT NULL,
  `orphan_status` int NOT NULL COMMENT 'SINGLE|DOUBLE|NONE, SINGLE: Single parent died. DOUBLE: Both parents died. NONE: All parents alive',
  `highest_education_level` int NOT NULL COMMENT 'Highest level of education completed: PRIMARY, SECONDARY, HIGH_SCHOOL, COLLEGE, UNIVERSITY, OTHER, NONE',
  `grade_level` int NOT NULL COMMENT 'School grade level if any',
  `school_name` varchar(100) DEFAULT NULL COMMENT 'Name of school enrolled at',
  `sourced_from` varchar(100) NOT NULL COMMENT 'Where this record was sourced from: i.e excel, or some other API service',
  PRIMARY KEY (`id`),
  UNIQUE KEY `individual_id_uid` (`individual_id`),
  KEY `location_code_fk` (`location_code`),
  CONSTRAINT `location_code_fk` FOREIGN KEY (`location_code`) REFERENCES `locations` (`code`)
);