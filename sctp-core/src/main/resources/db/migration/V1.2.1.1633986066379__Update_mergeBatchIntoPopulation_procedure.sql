DROP PROCEDURE IF EXISTS mergeBatchIntoPopulation;

DELIMITER $$
CREATE PROCEDURE mergeBatchIntoPopulation(IN _data_import_id bigint, IN new_status varchar(20))
comment 'Synchronize household and member data from ubr_csv_imports into households, individuals, and household_members tables.'
BEGIN
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

START TRANSACTION;

INSERT INTO households (
  household_id
, created_at
, location_code
, village_code
, ml_code
, ubr_code
, account_number
, floor_type
, roof_type
, wall_type
, latrine_type
, house_condition
, fuel_source
, water_source
, has_chair
, has_radio
, has_bicycles
, has_beds
, has_mattress
, has_sleeping_mat
, has_blankets
, has_water_can
, has_kitchen_utencils
, has_poultry
, has_livestock
, has_ox_cart
, has_hoe
, has_machete_knife
, has_mortar
, has_cellphone
, has_no_assets
, maize_harvest_lasted
, maize_in_granary_will_last
, meals_eaten_last_week
, has_latrine
, has_flush_toilet
, has_vip_latrine
, has_latrine_with_roof
, has_other_toilet_type
, receives_monetary_assistance
, survives_on_begging
, survives_on_ganyu
, survives_on_petty_trading
, survives_on_agriculture
, survives_on_other
, ta_code
, cluster_code
, zone_code
, assistance_received
, pmt_score
, wealth_quintile
, labor_constrained
, dependency_ratio
, ubr_csv_batch_number
)
SELECT
uci.household_id,
CURRENT_TIMESTAMP()
,uci.district_code
,uci.village_code
, uci.household_ml_code /* Refer to trigger definition generateHouseholdMlCode. */
,uci.form_number
,'n/a'
,uci.floor_type
,uci.roof_type
,uci.wall_type
,uci.latrine_type
,uci.house_condition
,uci.fuel_source
,uci.water_source
,uci.has_chair
,uci.has_radio
,uci.has_bicycles
,uci.has_beds
,uci.has_mattress
,uci.has_sleeping_mat
,uci.has_blankets
,uci.has_water_can
,uci.has_kitchen_utencils
,uci.has_poultry
,uci.has_livestock
,uci.has_ox_cart
,uci.has_hoe
,uci.has_machete_knife
,uci.has_mortar
,uci.has_cellphone
,uci.has_no_assets
,uci.last_harvest
,uci.current_harvest
,uci.meals
,uci.has_latrine
,uci.has_flush_toilet
,uci.has_vip_latrine
,uci.has_latrine_with_roof
,uci.has_other_toilet_type
,uci.assistance_received
,uci.survives_on_begging
,uci.survives_on_ganyu
,uci.survives_on_petty_trading
,uci.survives_on_agriculture
,uci.survives_on_other
,uci.ta_code
,uci.cluster_code
,uci.zone_code
,uci.assistance_received
,uci.pmt_score
,uci.wealth_quintile
,uci.labor_constrained
,uci.dependency_ratio
,uci.batch_number
FROM distinct_ubr_csv_import_household_records_v uci
WHERE (uci.data_import_id = _data_import_id and uci.archived = false and uci.validation_status = 'Valid')
ON duplicate KEY
UPDATE
 households.modified_at = CURRENT_TIMESTAMP()
,households.floor_type = uci.floor_type
,households.roof_type = uci.roof_type
,households.wall_type = uci.wall_type
,households.latrine_type = uci.latrine_type
,households.house_condition = uci.house_condition
,households.fuel_source = uci.fuel_source
,households.water_source = uci.water_source
,households.has_chair = uci.has_chair
,households.has_radio = uci.has_radio
,households.has_bicycles = uci.has_bicycles
,households.has_beds = uci.has_beds
,households.has_mattress = uci.has_mattress
,households.has_sleeping_mat = uci.has_sleeping_mat
,households.has_blankets = uci.has_blankets
,households.has_water_can = uci.has_water_can
,households.has_kitchen_utencils = uci.has_kitchen_utencils
,households.has_poultry = uci.has_poultry
,households.has_livestock = uci.has_livestock
,households.has_ox_cart = uci.has_ox_cart
,households.has_hoe = uci.has_hoe
,households.has_machete_knife = uci.has_machete_knife
,households.has_mortar = uci.has_mortar
,households.has_cellphone = uci.has_cellphone
,households.has_no_assets = uci.has_no_assets
,households.maize_harvest_lasted = uci.last_harvest
,households.maize_in_granary_will_last = uci.current_harvest
,households.meals_eaten_last_week = uci.meals
,households.has_latrine = uci.has_latrine
,households.has_flush_toilet = uci.has_flush_toilet
,households.has_vip_latrine = uci.has_vip_latrine
,households.has_latrine_with_roof = uci.has_latrine_with_roof
,households.has_other_toilet_type = uci.has_other_toilet_type
,households.receives_monetary_assistance = uci.assistance_received
,households.survives_on_begging = uci.survives_on_begging
,households.survives_on_ganyu = uci.survives_on_ganyu
,households.survives_on_petty_trading = uci.survives_on_petty_trading
,households.survives_on_agriculture = uci.survives_on_agriculture
,households.survives_on_other = uci.survives_on_other
,households.ta_code = uci.ta_code
,households.cluster_code = uci.cluster_code
,households.zone_code = uci.zone_code
,households.assistance_received = uci.assistance_received
,households.pmt_score = uci.pmt_score
,households.wealth_quintile = uci.wealth_quintile
,households.labor_constrained = uci.labor_constrained
,households.dependency_ratio = uci.dependency_ratio
,households.ubr_csv_batch_number = uci.batch_number;

INSERT INTO individuals (
first_name
, last_name
, middle_name
, date_of_birth
, is_estimated_dob
, location_code
, deleted
, status
, gender
, marital_status
, individual_id
, id_issue_date
, id_expiry_date
, created_at
, modified_at
, deleted_at
, phone_number
, disability
, chronic_illness
, fit_for_work
, orphan_status
, highest_education_level
, sourced_from
, ubr_csv_batch_number
, relationship_to_head
, ubr_household_member_id
, household_id
, household_code
)
SELECT
 t1.first_name
, t1.last_name
, NULL
, t1.date_of_birth
, FALSE
, t1.village_code
, FALSE
, 1
, t1.gender
, t1.marital_status
, COALESCE (t1.national_id, t1.sct_member_code, CONCAT('UBR-M-', t1.household_member_id))
, NULL  /* extract from national_id_barcode if possible */
, NULL  /* extract from national_id_barcode if possible */
, CURRENT_TIMESTAMP()
, NULL
, NULL
, t1.member_mobile_number
, t1.disability
, t1.chronic_illness
, t1.fit_for_work
, t1.orphan_status
, t1.highest_education_level
, 'UBR (CSV)'
, t1.batch_number
, t1.relationship_to_head
, t1.household_member_id
, did.household_id
, (select ml_code from households where household_id = t1.household_id limit 1)
FROM ubr_csv_imports t1
JOIN distinct_ubr_csv_import_household_records_v did ON did.batch_number = t1.batch_number AND did.data_import_id = _data_import_id
WHERE (t1.data_import_id = _data_import_id and t1.archived = false and t1.validation_status = 'Valid')
ON duplicate KEY UPDATE
individuals.first_name = t1.first_name
,individuals.last_name = t1.last_name
,individuals.date_of_birth = t1.date_of_birth
,individuals.location_code = t1.district_code
,individuals.status = 1
,individuals.gender = t1.gender
,individuals.marital_status = t1.marital_status
,individuals.modified_at = current_timestamp()
,individuals.phone_number = t1.member_mobile_number
,individuals.disability = t1.disability
,individuals.chronic_illness = t1.chronic_illness
,individuals.fit_for_work = t1.fit_for_work
,individuals.orphan_status = t1.orphan_status
,individuals.highest_education_level = t1.highest_education_level
,individuals.ubr_csv_batch_number = t1.batch_number
,individuals.household_id = t1.household_id ;

/*UPDATE status*/
UPDATE data_imports di SET di.status  = new_status WHERE id = _data_import_id;

COMMIT;
END$$

DELIMITER ;