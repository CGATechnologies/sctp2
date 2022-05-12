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

import org.cga.sctp.transfers.accounts.TransferAccountNumberList;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransferServiceImplTest {

    @Autowired
    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void initiateTransfers() {
        fail("not implemented yet");
    }

    @Test
    void fetchTransferList() {
        fail("not implemented yet");
    }

    @Test
    void fetchPendingTransferListByLocation() {
        fail("not implemented yet");
    }

    @Test
    void reconcileTransfers() {
        fail("not implemented yet");
    }

    @Test
    void updateTransferCalculations() {
        fail("not implemented yet");
    }

    @Test
    void updatePerformedTransfers() {
        fail("not implemented yet");
    }

    @Test
    void performManualTransfers() {
        TransferSession transferSession = new TransferSession();
        TransferPeriod transferPeriod = new TransferPeriod();
        TransferAccountNumberList accountNumberList = new TransferAccountNumberList();

        assertEquals(10, transferService.assignAccountNumbers(transferSession, transferPeriod, accountNumberList));
    }

    @Test
    void closeTransfers() {
        fail("not implemented yet");
    }

    @Test
    void assignAccountNumbers() {
        fail("not implemented yet");
    }

    @Test
    void exportTransferList() {
        fail("not implemented yet");
    }

    @Test
    void findAllActiveSessions() {
        fail("not implemented yet");
    }

    @Test
    void findAllHouseholdsInSession() {
        fail("not implemented yet");
    }
}