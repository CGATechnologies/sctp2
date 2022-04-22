-- Transfer Periods table
-- Links a Transfers to Transfer agencies and sets the period of the transfer
-- Equivalent to term table in the old database
CREATE TABLE transfer_periods (
    id bigint not null auto_increment primary key,
    program_id bigint not null,
    transfer_agency_id bigint not null,
    start_date date not null,
    end_date date not null,
    name varchar(100) not null,
    description varchar(100) not null,
    bonus_primary bigint,
    bonus_secondary bigint,
    opened_by bigint not null comment 'User who opened the transfer period',
    created_at timestamp,
    updated_at timestamp
);