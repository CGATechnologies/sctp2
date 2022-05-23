alter table targeting_sessions
    add column scm tinyint not null default 0 comment 'whether second community meeting has been done',
    add column dm tinyint not null default 0 comment 'district meeeting status',
    add column scm_user_id bigint comment 'user id under which the second community meeting update took place',
    add column dm_user_id bigint comment 'user id under which the district community meeting update took place',
    add column scm_timestamp timestamp,
    add column dm_timestamp timestamp,
    modify clusters longtext
;

CREATE INDEX ts_app_dm ON targeting_sessions (dm);
CREATE INDEX ts_app_scm ON targeting_sessions (scm);