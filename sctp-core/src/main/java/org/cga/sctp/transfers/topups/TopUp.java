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
import org.cga.sctp.targeting.LongSetConverter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name="transfer_topups")
@ValidTopupTypeAndAmount
public class TopUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Topup name must be specified")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Program must be specified")
    @Column(name = "program_id")
    private Long programId;

    @NotNull
    @Column(name = "funder_id")
    private Long funderId;

    @Column(name = "district_code")
    private Long districtCode;

    @Column(name = "ta_codes")
    @Convert(converter = LongSetConverter.class)
    private Set<Long> taCodes;

    @Column(name = "cluster_codes")
    @Convert(converter = LongSetConverter.class)
    private Set<Long> clusterCodes;

    @Column(name = "location_type")
    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    /**
     * The top-up arrears are discounted from the SCTP funds to be requested'
     */
    @Column(name = "is_discounted_from_funds")
    private boolean isDiscountedFromFunds;

    /**
     * Whether topup is categorical or not
     */
    @Column(name = "is_categorical")
    private boolean isCategorical;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_executed")
    private boolean isExecuted;

    @Column(name = "topup_type")
    @Convert(converter = TopUpType.Converter.class)
    private TopUpType topupType;

    @Column
    @Convert(converter = TopUpHouseholdStatus.Converter.class)
    private TopUpHouseholdStatus householdStatus;

    /**
     * Percentage amount only applies for {@link TopUpType#PERCENTAGE_OF_RECIPIENT_AMOUNT}
     */
    @Column(name = "percentage")
    private BigDecimal percentage;

    @Column(name = "categorical_targeting_criteria_id")
    private Long categoricalTargetingCriteriaId;

    /**
     * Fixed amount only applies for {@link TopUpType#FIXED_AMOUNT}
     */
    @Column(name = "fixed_amount")
    private BigDecimal fixedAmount;


    @Column(name = "amount_projected")
    private BigDecimal amountProjected;

    /**
     * Amount executed is the amount actually paid out from this TopUp to households
     */
    @Column(name = "amount_executed")
    private BigDecimal amountExecuted;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

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

    public Long getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Long districtCode) {
        this.districtCode = districtCode;
    }

    public Set<Long> getTaCodes() {
        return taCodes;
    }

    public void setTaCodes(Set<Long> taCodes) {
        this.taCodes = taCodes;
    }

    public Set<Long> getClusterCodes() {
        return clusterCodes;
    }

    public void setClusterCodes(Set<Long> clusterCodes) {
        this.clusterCodes = clusterCodes;
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

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public Long getCategoricalTargetingCriteriaId() {
        return categoricalTargetingCriteriaId;
    }

    public void setCategoricalTargetingCriteriaId(Long categoricalTargetingCriteriaId) {
        this.categoricalTargetingCriteriaId = categoricalTargetingCriteriaId;
    }

    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public BigDecimal getAmountProjected() {
        return amountProjected;
    }

    public void setAmountProjected(BigDecimal amountProjected) {
        this.amountProjected = amountProjected;
    }

    public BigDecimal getAmountExecuted() {
        return amountExecuted;
    }

    public void setAmountExecuted(BigDecimal amountExecuted) {
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TopUp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", programId=" + programId +
                ", funderId=" + funderId +
                ", districtCode=" + districtCode +
                ", taCodes=" + taCodes +
                ", clusterCodes=" + clusterCodes +
                ", locationType=" + locationType +
                ", isDiscountedFromFunds=" + isDiscountedFromFunds +
                ", isCategorical=" + isCategorical +
                ", isActive=" + isActive +
                ", isExecuted=" + isExecuted +
                ", topupType=" + topupType +
                ", householdStatus=" + householdStatus +
                ", percentage=" + percentage +
                ", categoricalTargetingCriteriaId=" + categoricalTargetingCriteriaId +
                ", fixedAmount=" + fixedAmount +
                ", amountProjected=" + amountProjected +
                ", amountExecuted=" + amountExecuted +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
