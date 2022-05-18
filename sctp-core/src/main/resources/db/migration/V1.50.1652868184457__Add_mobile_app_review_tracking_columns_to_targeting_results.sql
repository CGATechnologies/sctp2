-- these columns will help in keeping track of when a household's status was updated from the mobile app.
-- parent session will refer to these fields to indicate whether the session was reviewed on the mobile app.

alter table targeting_results
    add column scm_user_id bigint
        comment 'indicates which user posted the second community meeting changes from the mobile app',
    add column scm_user_timestamp timestamp
        comment 'timestamp of when the second community meeting changes were received',

    add column dm_user_id bigint
        comment 'indicates which user posted the second district meeting changes from the mobile app',
    add column dm_user_timestamp timestamp
        comment 'timestamp of when the district meeting changes were received',

    ADD CONSTRAINT `fk_targeting_results_scm_user_id` FOREIGN KEY (scm_user_id) REFERENCES users(id),
    ADD CONSTRAINT `fk_targeting_results_dm_user_id` FOREIGN KEY (dm_user_id) REFERENCES users(id)
;



