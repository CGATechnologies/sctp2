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

package org.cga.sctp.transfers;

import org.cga.sctp.location.Location;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.transfers.parameters.EducationTransferParameter;
import org.cga.sctp.transfers.parameters.HouseholdParameterCondition;
import org.cga.sctp.transfers.parameters.HouseholdTransferParameter;
import org.cga.sctp.transfers.parameters.TransferParametersService;
import org.cga.sctp.transfers.periods.TransferPeriod;

import java.util.List;
import java.util.Optional;

public class TransferCalculator {
    private List<EducationTransferParameter> educationTransferParameters;
    private List<HouseholdTransferParameter> householdTransferParameters;

    public TransferCalculator(List<EducationTransferParameter> educationTransferParameters, List<HouseholdTransferParameter> householdTransferParameters) {
        this.educationTransferParameters = educationTransferParameters;
        this.householdTransferParameters = householdTransferParameters;
    }


    public Long getSecondaryBonusAmount() {
        // TODO: don't fetch from the db on every call/invocation of this method
        return getSecondaryBonusAmount(educationTransferParameters);
    }

    public Long getSecondaryBonusAmount(List<EducationTransferParameter> parameters) {
        Optional<EducationTransferParameter> educationTransferParameterOptional = parameters.stream()
                .filter(e -> e.getEducationLevel().equals(EducationLevel.Secondary))
                .findFirst();

        if (educationTransferParameterOptional.isEmpty()) {
            return 0L;
        }

        return educationTransferParameterOptional.get().getAmount();
    }

    public Long getPrimaryBonusAmount() {
        // TODO: don't fetch from the db on every call/invocation of this method
        return this.getPrimaryBonusAmount(educationTransferParameters);
    }

    public Long getPrimaryBonusAmount(List<EducationTransferParameter> educationTransferParameters) {
        Optional<EducationTransferParameter> educationTransferParameterOptional = educationTransferParameters
                .stream()
                .filter(e -> e.getEducationLevel().equals(EducationLevel.Primary))
                .findFirst();

        if (educationTransferParameterOptional.isEmpty()) {
            return 0L;
        }

        return educationTransferParameterOptional.get().getAmount();
    }

    public Long getPrimaryIncentiveAmount() {
        // NOTE: At the moment the primary bonus amount is the same as the incentive.
        return getPrimaryBonusAmount();
    }

    public Long getPrimaryIncentiveAmount(List<EducationTransferParameter> educationTransferParameters) {
        return getPrimaryBonusAmount(educationTransferParameters);
    }

    /**
     * Get the basic amount to be disbursed to the household according to it's size
     * @param householdSize size of the household
     * @param householdParams household params
     * @return amount
     */
    public Long determineAmountByHouseholdSize(int householdSize, List<HouseholdTransferParameter> householdParams) {
        for(var param : householdParams) {
            if (param.getCondition().equals(HouseholdParameterCondition.GREATER_THAN) &&
                    householdSize > param.getNumberOfMembers()) {

                return param.getAmount();

            } else if (param.getCondition().equals(HouseholdParameterCondition.GREATER_THAN_OR_EQUALS) &&
                    householdSize >= param.getNumberOfMembers()) {

                return param.getAmount();
            } else if (param.getCondition().equals(HouseholdParameterCondition.EQUALS) &&
                    householdSize == param.getNumberOfMembers()) {

                return param.getAmount();
            }
        }

        return -1L;
    }

    /**
     * Calculates transfers for all the households in the transfer period for a given location..
     * @param transferPeriod
     */
     public void calculateTransfersUpdate(TransferPeriod transferPeriod, List<Transfer> transferList) {
        long monthlyAmount = 0;
        long basicAmount, secondaryBonus, primaryBonus, primaryIncentive;
        for(var transfer: transferList) {
            if (transfer.getTransferPeriodId() != transferPeriod.getId()) {
                continue;
            }
            basicAmount = determineAmountByHouseholdSize(transfer.getHouseholdMemberCount(), householdTransferParameters);
            secondaryBonus = transfer.getSecondaryChildrenCount() * getSecondaryBonusAmount(educationTransferParameters);
            primaryBonus = transfer.getPrimaryChildrenCount() * getPrimaryBonusAmount(educationTransferParameters);
            // TODO: store the number of children that need/want incentives for primary school...
            primaryIncentive = transfer.getPrimaryIncentiveChildrenCount() * getPrimaryIncentiveAmount();

            transfer.setNumberOfMonths(transferPeriod.countNoOfMonths());
            transfer.setAmountDisbursed(0L);
            transfer.setTopupAmount(0L);
            transfer.setBasicSubsidyAmount(basicAmount);
            transfer.setPrimaryIncentiveAmount(primaryIncentive);
            // TODO: transfer.setPrimaryBonusAmount(primaryIncentive);
            transfer.setSecondaryBonusAmount(secondaryBonus);
            // TODO: Should we set this or calculate upon request
            monthlyAmount = basicAmount + primaryBonus + secondaryBonus + primaryIncentive;
        }
    }
}
