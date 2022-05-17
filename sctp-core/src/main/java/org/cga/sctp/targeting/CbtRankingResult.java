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

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * <p>Community based targeting record (from view)</p>
 * <p>Ideally, should be {@link TargetingResult}. The {@link TargetingResult} class came later after refactoring</p>
 */
@Entity
@Immutable
@Table(name = "cbt_ranking_results_v")
public class CbtRankingResult {
    @Id
    @Column
    private Long householdId;

    @Column
    private String householdHead;

    @Column
    private int memberCount;

    @Column
    private Integer rank;

    @Column
    private String taName;

    @Column
    private String zoneName;

    @Column
    private String formNumber;

    @Column
    private Long cbtSessionId;

    @Column
    private String clusterName;

    @Column
    private String villageName;

    @Column
    private BigDecimal pmtScore;

    @Column
    private String districtName;

    @Column
    @Convert(disableConversion = true)
    @Enumerated(EnumType.STRING)
    private CbtStatus status;

    @Column
    private String villageHeadName;

    @Column
    private String mlCode;

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdHead() {
        return householdHead;
    }

    public void setHouseholdHead(String householdHead) {
        this.householdHead = householdHead;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getTaName() {
        return taName;
    }

    public void setTaName(String taName) {
        this.taName = taName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(String formNumber) {
        this.formNumber = formNumber;
    }

    public Long getCbtSessionId() {
        return cbtSessionId;
    }

    public void setCbtSessionId(Long cbtSessionId) {
        this.cbtSessionId = cbtSessionId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public BigDecimal getPmtScore() {
        return pmtScore;
    }

    public void setPmtScore(BigDecimal pmtScore) {
        this.pmtScore = pmtScore;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public CbtStatus getStatus() {
        return status;
    }

    public void setStatus(CbtStatus status) {
        this.status = status;
    }

    public String getVillageHeadName() {
        return villageHeadName;
    }

    public void setVillageHeadName(String villageHeadName) {
        this.villageHeadName = villageHeadName;
    }

    public String getMlCode() {
        return mlCode;
    }

    public void setMlCode(String mlCode) {
        this.mlCode = mlCode;
    }
}
