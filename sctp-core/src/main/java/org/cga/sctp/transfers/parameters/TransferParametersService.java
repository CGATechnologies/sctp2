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

package org.cga.sctp.transfers.parameters;

import org.cga.sctp.transfers.parameters.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferParametersService {
    @Autowired
    private HouseholdTransferParametersRepository householdTransferParametersRepository;

    @Autowired
    private LocationTransferParametersRepository locationTransferParametersRepository;

    @Autowired
    private EducationTransferParameterRepository educationTransferParameterRepository;

    public List<HouseholdTransferParameter> findAllActiveHouseholdParameters() {
        return householdTransferParametersRepository.findAll();
    }

    public List<EducationTransferParameter> findAllEducationTransferParameters() {
        return educationTransferParameterRepository.findAll();
    }

    /**
     * Get the basic amount to be disbursed to the household according to it's size
     * @param householdSize number of members in the household
     * @return amount to give
     */
    public Long determineAmountByHouseholdSize(int householdSize) {
        List<HouseholdTransferParameter> householdParams = householdTransferParametersRepository.findAll();
        return determineAmountByHouseholdSize(householdSize, householdParams);
    }

    /**
     * Get the basic amount to be disbursed to the household according to it's size
     * @param householdSize size of the household
     * @param householdParams household params
     * @return amount
     */
    Long determineAmountByHouseholdSize(int householdSize, List<HouseholdTransferParameter> householdParams) {
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
}
