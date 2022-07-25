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

package org.cga.sctp.transfers.topups;

import org.cga.sctp.location.LocationType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="transfer_topups")
public class TopUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  id BIGINT(19) PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'Primary key',

    @Column(name = "name")
    private String name; // name VARCHAR(90) NULL DEFAULT NULL COMMENT 'Name of topup' COLLATE 'utf8_unicode_ci',

    @Column(name = "program_id")
    private Long programId; // Program for the topup

    @Column(name = "funder_id")
    private Long funderId; // funder_id BIGINT(19) NOT NULL COMMENT 'Funding institution for this topup',

    @Column(name = "location_id")
    private Long locationId; // location_id INT(10) NOT NULL COMMENT 'FOREIGN KEY Table geolocation Field geo_id, District',

    @Column(name = "location_type")
    @Enumerated(EnumType.STRING)
    private LocationType locationType; // location_type INT(10) NOT NULL COMMENT 'FOREIGN KEY Table item Field ite_id, Level',

    @Column(name = "is_discounted_from_funds")
    private boolean isDiscountedFromFunds; // is_discounted_from_funds TINYINT(1) NULL DEFAULT NULL COMMENT 'The top-up arrears are discounted from the SCTP funds to be requested',

    @Column(name = "is_categorical")
    private boolean isCategorical; // is_categorical TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Whether topup is categorical or not',

    @Column(name = "is_active")
    private boolean isActive; // is_active  TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Active or not 1 = yes, 0 = no',

    @Column(name = "is_executed")
    private boolean isExecuted; // is_executed TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Executed or not 1 = yes, 0 = no',

    @Column(name = "topup_type")
    @Convert(converter = TopUpType.Converter.class)
    private TopUpType topupType; // topup_type TINYINT(1) NOT NULL DEFAULT '0' COMMENT 'Topup type, See TopupType enumeration',

    @Column
    @Convert(converter = TopUpHouseholdStatus.Converter.class)
    private TopUpHouseholdStatus householdStatus;

    @Column(name = "percentage")
    private double percentage; // percentage DOUBLE COMMENT 'For percentage based topups, the percentage to use for calculation',

    @Column(name = "categorical_targeting_criteria_id")
    private Long categoricalTargetingCriteriaId; // categorical_targeting_criteria_id BIGINT(19) NULL COMMENT 'For categorical topups, the criteria to use.',

    @Column(name = "amount")
    private Long amount; // amount BIGINT(19) NULL DEFAULT NULL COMMENT 'Amount to topup',

    @Column(name = "amount_projected")
    private Long amountProjected; // amount_projected BIGINT(19) NULL DEFAULT NULL COMMENT 'Amount projected',

    @Column(name = "amount_executed")
    private Long amountExecuted; // amount_executed BIGINT(19) NULL DEFAULT NULL COMMENT 'Amount executed i.e. disbursed',

    @Column(name = "created_by")
    private Long createdBy; // created_by BIGINT(19) NOT NULL COMMENT 'User who created the topup',

    @Column(name = "updated_by")
    private Long updatedBy; // updated_by BIGINT(19) NOT NULL COMMENT 'User who updated the topup',

    @Column(name = "created_at")
    private LocalDateTime createdAt; // created_at timestamp not null,

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // updated_at timestamp not null

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFunderId() {
        return funderId;
    }

    public void setFunderId(Long funderId) {
        this.funderId = funderId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public boolean isDiscountedFromFunds() {
        return isDiscountedFromFunds;
    }

    public void setDiscountedFromFunds(boolean discountedFromFunds) {
        isDiscountedFromFunds = discountedFromFunds;
    }

    public boolean isCategorical() {
        return isCategorical;
    }

    public void setCategorical(boolean categorical) {
        isCategorical = categorical;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

    public TopUpType getTopupType() {
        return topupType;
    }

    public void setTopupType(TopUpType topupType) {
        this.topupType = topupType;
    }

    public TopUpHouseholdStatus getHouseholdStatus() {
        return householdStatus;
    }

    public void setHouseholdStatus(TopUpHouseholdStatus householdStatus) {
        this.householdStatus = householdStatus;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Long getCategoricalTargetingCriteriaId() {
        return categoricalTargetingCriteriaId;
    }

    public void setCategoricalTargetingCriteriaId(Long categoricalTargetingCriteriaId) {
        this.categoricalTargetingCriteriaId = categoricalTargetingCriteriaId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getAmountProjected() {
        return amountProjected;
    }

    public void setAmountProjected(Long amountProjected) {
        this.amountProjected = amountProjected;
    }

    public Long getAmountExecuted() {
        return amountExecuted;
    }

    public void setAmountExecuted(Long amountExecuted) {
        this.amountExecuted = amountExecuted;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
