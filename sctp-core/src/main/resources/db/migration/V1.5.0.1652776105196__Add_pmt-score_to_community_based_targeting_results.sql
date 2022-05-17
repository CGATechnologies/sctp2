alter table targeting_results
    add column pmt_score numeric(12,6) not null
        comment 'Captured pmt_score that warranted the houshold ranking'
        after ranking
;