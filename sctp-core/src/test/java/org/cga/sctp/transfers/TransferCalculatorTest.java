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

import org.apache.poi.ss.formula.functions.T;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.transfers.parameters.EducationTransferParameter;
import org.cga.sctp.transfers.parameters.HouseholdParameterCondition;
import org.cga.sctp.transfers.parameters.HouseholdTransferParameter;
import org.cga.sctp.transfers.parameters.TransferParametersService;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class TransferCalculatorTest {

    @Test
    void calculateTransfersUpdate() {
        TransferParametersService service = new TransferParametersService();

        List<HouseholdTransferParameter> householdTransferParameters = List.of(
                createParam(1, 1000L, HouseholdParameterCondition.EQUALS),
                createParam(2, 2000L, HouseholdParameterCondition.EQUALS),
                createParam(3, 3000L, HouseholdParameterCondition.EQUALS),
                createParam(4, 4000L, HouseholdParameterCondition.GREATER_THAN_OR_EQUALS)
        );
        List<EducationTransferParameter> educationTransferParameters = List.of(
                createEducationParam(2000L, EducationLevel.Primary),
                createEducationParam(1000L, EducationLevel.Secondary)
        );

        Location location = new Location();
        location.setId(328L);
        location.setLocationType(LocationType.SUBNATIONAL1);
        location.setName("Nkhatabay");
        location.setCode(328L);
        location.setActive(true);

        TransferPeriod transferPeriod = new TransferPeriod();
        transferPeriod.setId(1L);
        transferPeriod.setBonusPrimaryParameterId(0L);
        transferPeriod.setBonusSecondaryParameterId(0L);
        transferPeriod.setClosed(false);
        transferPeriod.setDescription("Test");
        transferPeriod.setDistrictId(328);
        transferPeriod.setStartDate(LocalDate.now());
        transferPeriod.setEndDate(LocalDate.now().plusMonths(2));
        transferPeriod.setName("Test Transfer Period");
        transferPeriod.setOpenedBy(1L);
        transferPeriod.setProgramId(1L);
        transferPeriod.setTransferSessionId(1L);
        transferPeriod.setCreatedAt(LocalDateTime.now());
        transferPeriod.setUpdatedAt(LocalDateTime.now());

        Transfer transfer = new Transfer();
        transfer.setTransferPeriodId(transferPeriod.getId());
        transfer.setNumberOfMonths(1L);
        transfer.setHouseholdMemberCount(3);
        transfer.setPrimaryChildrenCount(1L);
        transfer.setPrimaryIncentiveChildrenCount(1L);
        transfer.setSecondaryChildrenCount(1L);

        TransferCalculator transferCalculator = new TransferCalculator(educationTransferParameters, householdTransferParameters);
        transferCalculator.calculateTransfersUpdate(transferPeriod, Collections.singletonList(transfer));

        assertEquals(3000L + 2000L + 1000L, transfer.getTotalAmountToTransfer());
    }

    private static HouseholdTransferParameter createParam(int members, Long amount, HouseholdParameterCondition condition) {
        HouseholdTransferParameter p = new HouseholdTransferParameter();
        p.setAmount(amount);
        p.setNumberOfMembers(members);
        p.setCondition(condition);
        return p;
    }

    private static EducationTransferParameter createEducationParam(Long amount, EducationLevel level) {
        EducationTransferParameter p = new EducationTransferParameter();
        p.setAmount(amount);
        p.setEducationLevel(level);
        return p;
    }
}