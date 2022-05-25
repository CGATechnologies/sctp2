alter table targeting_sessions
    add column meeting_phase enum('second_community_meeting', 'district_meeting', 'completed')
        not null default 'second_community_meeting'
        comment 'specifies which meeting phase this targeting session is at.';

-- Migrate status
update targeting_sessions
    set meeting_phase = 'second_community_meeting'
    where scm = false and dm = false and status = 'Review'
;

update targeting_sessions
    set meeting_phase = 'district_meeting'
    where scm = true and dm = false and status = 'Review'
;

update targeting_sessions
    set meeting_phase = 'completed'
    where scm = true and dm = true and (status = 'Closed' or status = 'Review')
;

alter table targeting_sessions
    drop column scm,
    drop column dm
;