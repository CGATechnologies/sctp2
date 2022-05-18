alter table targeting_results
    add column pev_session bigint
        comment 'if this session was automatically created from a pre-eligibiligy verification run, the id will be stored here'
        after id
;