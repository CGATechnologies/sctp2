--
-- Table for temporarily holding information before being imported into main population table
--

CREATE TABLE IF NOT EXISTS ubr_csv_imports (
	id bigint AUTO_INCREMENT NOT NULL,
    data_import_id bigint NOT NULL,
    created_at timestamp NOT NULL,

    validation_status varchar(30),
    status_text varchar(100),

    household_id bigint,
    form_number varchar(20),

 	household_code varchar(20),
    registration_date date comment 'Registration date in UBR',
    district_code int,
    gps_latitude decimal(11, 8),
    gps_longitude decimal(11, 8),
    programmes varchar(1024),
    firstName varchar(50),
    lastName varchar(50),

    gender int,
    highest_education_level int,

    fit_for_work boolean,

    sct_member_code varchar(20),

    date_of_birth date,

    relationship_to_head int,

    national_id varchar(8),

    member_mobile_number varchar(10),

    house_ownership int,

    house_type int,

    house_condition int,

    wall_type int,

    floor_type int,

    latrine_type int,

    fuel_source int,

    `has_chair` tinyint,
	`has_radio` tinyint,
	`has_bicycles` tinyint,
	`has_beds` tinyint,
	`has_mattress` tinyint,
	`has_sleeping_mat` tinyint,
	`has_blankets` tinyint,
	`has_water_can` tinyint,
	`has_kitchen_utencils` tinyint,
	`has_poultry` tinyint,
	`has_livestock` tinyint,
	`has_ox_cart` tinyint,
	`has_hoe` tinyint,
	`has_machete_knife` tinyint,
	`has_mortar` tinyint,
	`has_cellphone` tinyint ,
	`has_no_assets` tinyint,
	`has_latrine` tinyint ,
	`has_flush_toilet` tinyint,
	`has_vip_latrine` tinyint,
	`has_latrine_with_roof` tinyint,
	`has_other_toilet_type` tinyint,

    survives_on_begging BOOLEAN,
	survives_on_ganyu BOOLEAN,
	survives_on_petty_trading BOOLEAN,
	survives_on_agriculture BOOLEAN,
	survives_on_other BOOLEAN,

    last_harvest int comment 'How long last harvest lasted',

    current_harvest int comment 'How long current harvest will last',

    meals int comment 'Meals per day',

   PRIMARY KEY (id),
   CONSTRAINT data_import_id_fk FOREIGN key(data_import_id) REFERENCES data_imports (id)
);
