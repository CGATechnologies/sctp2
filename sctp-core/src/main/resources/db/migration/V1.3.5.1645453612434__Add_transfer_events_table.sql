CREATE TABLE transfers_events (
  `id` bigint primary key auto_increment not null,
  `program_id` bigint,
  `enrollment_session_id` bigint,
  `transfer_session_id` bigint,
  `household_id` bigint,
  `recipient_id` bigint,
  `zone_id` bigint,
  `village_cluster_id` bigint,
  `account_number` varchar(50),
  `first_transfer` tinyint(1),
  `collected` tinyint(1),
  `transfer_received` tinyint(1),
  `suspended` tinyint(1),
  `non_recertified` tinyint(1),
  `modality` varchar(50),
  `subsidy_amount` bigint,
  `arrears_uncollected_amount` bigint,
  `arrears_untransferred_amount` bigint,
  `arrears_updated_amount` bigint,
  `arrears_amount` bigint,
  `total_transfer_amount` bigint,
  `total_members` bigint,
  `total_members_primary` bigint,
  `total_members_primary_incentive` bigint,
  `total_members_secondary` bigint,
  `topup` tinyint(1),
  `topup_value` bigint,
  `value_arrears_topup` bigint,
  `value_arrearstopup_receive` bigint,
  `has_changed_geolocation` tinyint(1),
  `replaced` tinyint(1),
  `transfer_field_work` tinyint(1),
  `datefieldwork` datetime,
  `field_work_user_id` bigint,
  `upload_reconciliation` tinyint(1),
  `date_reconciled` datetime,
  `transfer_status` int not null,
  `transfer_household_state` int,
  `created_at` timestamp not null,
  `modified_at` timestamp not null
);