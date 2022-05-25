/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.targeting;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

@MappedSuperclass
public class TargetingSessionBase {
    public enum SessionStatus {
        Review,
        Closed
    }

    public enum MeetingPhase {
        completed,
        district_meeting,
        second_community_meeting
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Long closedBy;
    private Long createdBy;
    private Long districtCode;
    private Long programId;
    private Long taCode;
    @Column(name = "pev_session")
    private Long pevSession;

    @Enumerated(EnumType.STRING)
    private MeetingPhase meetingPhase;

    @Column(name = "scm_timestamp")
    private OffsetDateTime communityMeetingTimestamp;

    @Column(name = "dm_timestamp")
    private OffsetDateTime districtMeetingTimestamp;

    @Column(name = "dm_user_id")
    private Long districtMeetingUserId;

    @Column(name = "scm_user_id")
    private Long communityMeetingUserId;

    @Enumerated(EnumType.STRING)
    private TargetingSession.SessionStatus status;

    @Convert(converter = LongSetConverter.class)
    private Set<Long> clusters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public Long getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Long closedBy) {
        this.closedBy = closedBy;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Long districtCode) {
        this.districtCode = districtCode;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getTaCode() {
        return taCode;
    }

    public void setTaCode(Long taCode) {
        this.taCode = taCode;
    }

    public TargetingSession.SessionStatus getStatus() {
        return status;
    }

    public void setStatus(TargetingSession.SessionStatus status) {
        this.status = status;
    }

    public Set<Long> getClusters() {
        return clusters;
    }

    public void setClusters(Set<Long> clusters) {
        this.clusters = clusters;
    }

    public Long getPevSession() {
        return pevSession;
    }

    public void setPevSession(Long pevSession) {
        this.pevSession = pevSession;
    }

    @Transient
    @JsonIgnore
    public boolean isClosed() {
        return status == SessionStatus.Closed;
    }

    @Transient
    @JsonIgnore
    public boolean isOpen() {
        return status == SessionStatus.Review;
    }

    public OffsetDateTime getCommunityMeetingTimestamp() {
        return communityMeetingTimestamp;
    }

    public void setCommunityMeetingTimestamp(OffsetDateTime communityMeetingTimestamp) {
        this.communityMeetingTimestamp = communityMeetingTimestamp;
    }

    public OffsetDateTime getDistrictMeetingTimestamp() {
        return districtMeetingTimestamp;
    }

    public void setDistrictMeetingTimestamp(OffsetDateTime districtMeetingTimestamp) {
        this.districtMeetingTimestamp = districtMeetingTimestamp;
    }

    public Long getDistrictMeetingUserId() {
        return districtMeetingUserId;
    }

    public void setDistrictMeetingUserId(Long districtMeetingUserId) {
        this.districtMeetingUserId = districtMeetingUserId;
    }

    public Long getCommunityMeetingUserId() {
        return communityMeetingUserId;
    }

    public void setCommunityMeetingUserId(Long communityMeetingUserId) {
        this.communityMeetingUserId = communityMeetingUserId;
    }

    public MeetingPhase getMeetingPhase() {
        return meetingPhase;
    }

    public void setMeetingPhase(MeetingPhase meetingPhase) {
        this.meetingPhase = meetingPhase;
    }

    @Transient
    @JsonIgnore
    public boolean isAtDistrictMeeting() {
        return getStatus() == SessionStatus.Review
                && meetingPhase == MeetingPhase.district_meeting;
    }

    @Transient
    @JsonIgnore
    public boolean isAtSecondCommunityMeeting() {
        return getStatus() == SessionStatus.Review
                && meetingPhase == MeetingPhase.second_community_meeting;
    }
}
