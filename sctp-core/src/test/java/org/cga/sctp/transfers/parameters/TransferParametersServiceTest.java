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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferParametersServiceTest {
    private static HouseholdTransferParameter createParam(int members, Long amount, HouseholdParameterCondition condition) {
        HouseholdTransferParameter p = new HouseholdTransferParameter();
        p.setAmount(amount);
        p.setNumberOfMembers(members);
        p.setCondition(condition);
        return p;
    }
    @Test
    void determineAmountByHouseholdSize() {

        TransferParametersService service = new TransferParametersService();

        List<HouseholdTransferParameter> params = List.of(
                createParam(1, 1000L, HouseholdParameterCondition.EQUALS),
                createParam(2, 2000L, HouseholdParameterCondition.EQUALS),
                createParam(3, 3000L, HouseholdParameterCondition.EQUALS),
                createParam(4, 4000L, HouseholdParameterCondition.GREATER_THAN_OR_EQUALS)
        );

        assertEquals(1000, service.determineAmountByHouseholdSize(1, params));

        assertEquals(2000, service.determineAmountByHouseholdSize(2, params));

        assertEquals(3000, service.determineAmountByHouseholdSize(3, params));

        assertEquals(4000, service.determineAmountByHouseholdSize(4, params));

        assertEquals(4000, service.determineAmountByHouseholdSize(5, params));
    }
}