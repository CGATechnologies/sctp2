alter table individuals
    add column household_cold bigint comment 'Reference to the household';

alter table individuals
    add index hh_idx (household_cold);