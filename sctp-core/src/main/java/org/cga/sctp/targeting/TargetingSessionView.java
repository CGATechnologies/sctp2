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
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Immutable
@Entity
@Table(name = "target_sessions_view")
public class TargetingSessionView extends TargetingSessionBase {
    private String taName;
    private String closerName;
    private String programName;
    private String creatorName;
    private String districtName;
    private Long householdCount;

    @Column(name = "app_scm")
    @JsonIgnore
    private boolean appCommunityMeeting;

    @Column(name = "app_dm")
    @JsonIgnore
    private boolean appDistrictMeeting;

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getTaName() {
        return taName;
    }

    public void setTaName(String taName) {
        this.taName = taName;
    }

    public String getCloserName() {
        return closerName;
    }

    public void setCloserName(String closerName) {
        this.closerName = closerName;
    }

    public Long getHouseholdCount() {
        return householdCount;
    }

    public void setHouseholdCount(Long householdCount) {
        this.householdCount = householdCount;
    }

    public boolean isAppCommunityMeeting() {
        return appCommunityMeeting;
    }

    public void setAppCommunityMeeting(boolean appCommunityMeeting) {
        this.appCommunityMeeting = appCommunityMeeting;
    }

    public boolean isAppDistrictMeeting() {
        return appDistrictMeeting;
    }

    public void setAppDistrictMeeting(boolean appDistrictMeeting) {
        this.appDistrictMeeting = appDistrictMeeting;
    }

    public boolean isAtDistrictMeeting() {
        return getStatus() == SessionStatus.Review
                && appCommunityMeeting
                && !appDistrictMeeting;
    }

    public boolean isAtSecondCommunityMeeting() {
        return getStatus() == SessionStatus.Review
                && !appCommunityMeeting
                && !appDistrictMeeting;
    }
}
