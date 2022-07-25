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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class NewTopUpParams {
    @NotNull
    @NotEmpty(message = "name cannot be empty")
    private String name;

    @NotNull(message = "Program must be specified")
    private Long programId;

    @NotNull(message = "Sponsor / Funding institution must be specified")
    private Long funderId;

    @NotNull(message = "Location(s) must be specified")
    private Long locationId;

    @NotNull(message = "Location Type must be specified")
    private LocationType locationType;

    private double percentage;

    @NotNull(message = "Type of TopUp must be specified")
    private TopUpType topupType;

    @NotNull(message = "Please specify household status to get topups: whether both, recertified or non-recertified")
    private TopUpHouseholdStatus householdStatus;

    @NotNull
    private boolean active;

    @NotNull(message = "Amount must be provided")
    private Long amount;

    @NotNull(message = "Specify whether the topup is categorical or not")
    private boolean categorical;

    private Long categoricalTargetingCriteriaId;

    @NotNull(message = "Please specify whether amount will be discounted from the program Funds")
    private boolean discountedFromFunds;

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

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public boolean isCategorical() {
        return categorical;
    }

    public void setCategorical(boolean categorical) {
        this.categorical = categorical;
    }

    public Long getCategoricalTargetingCriteriaId() {
        return categoricalTargetingCriteriaId;
    }

    public void setCategoricalTargetingCriteriaId(Long categoricalTargetingCriteriaId) {
        this.categoricalTargetingCriteriaId = categoricalTargetingCriteriaId;
    }

    public boolean isDiscountedFromFunds() {
        return discountedFromFunds;
    }

    public void setDiscountedFromFunds(boolean discountedFromFunds) {
        this.discountedFromFunds = discountedFromFunds;
    }
}
