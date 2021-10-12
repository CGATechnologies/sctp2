alter table individuals
    drop index hh_idx;

alter table individuals
    change column household_cold household_code bigint comment 'Reference to the household ML code';

alter table individuals
    add index hh_idx (household_code);