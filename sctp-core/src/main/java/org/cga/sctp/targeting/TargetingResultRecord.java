/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, CGATechnologies
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

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@MappedSuperclass
public class TargetingResultRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "targeting_session", nullable = false)
    private Long targetingSession;

    @Column(name = "household_id", nullable = false)
    private Long household;

    @Column(name = "pmt_score")
    private BigDecimal pmtScore;

    @Column(name = "ranking", nullable = false)
    private Integer ranking;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Convert(disableConversion = true)
    private CbtStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * Second community meeting user id
     */
    @Column(name = "scm_user_id")
    private Long scmUserId;

    /**
     * Second community meeting timestamp
     */
    @Column(name = "scm_timestamp")
    private OffsetDateTime scmTimestamp;

    /**
     * District meeting user id
     */
    @Column(name = "dm_user_id")
    private Long dmUserId;

    /**
     * District meeting user id
     */
    @Column(name = "dm_timestamp")
    private OffsetDateTime dmTimestamp;

    public Long getScmUserId() {
        return scmUserId;
    }

    public void setScmUserId(Long scmUserId) {
        this.scmUserId = scmUserId;
    }

    public OffsetDateTime getScmTimestamp() {
        return scmTimestamp;
    }

    public void setScmTimestamp(OffsetDateTime scmTimestamp) {
        this.scmTimestamp = scmTimestamp;
    }

    public Long getDmUserId() {
        return dmUserId;
    }

    public void setDmUserId(Long dmUserId) {
        this.dmUserId = dmUserId;
    }

    public OffsetDateTime getDmTimestamp() {
        return dmTimestamp;
    }

    public void setDmTimestamp(OffsetDateTime dmTimestamp) {
        this.dmTimestamp = dmTimestamp;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CbtStatus getStatus() {
        return status;
    }

    public void setStatus(CbtStatus status) {
        this.status = status;
    }

    public BigDecimal getPmtScore() {
        return pmtScore;
    }

    public void setPmtScore(BigDecimal pmtScore) {
        this.pmtScore = pmtScore;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Long getHousehold() {
        return household;
    }

    public void setHousehold(Long household) {
        this.household = household;
    }

    public Long getTargetingSession() {
        return targetingSession;
    }

    public void setTargetingSession(Long targetingSession) {
        this.targetingSession = targetingSession;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}