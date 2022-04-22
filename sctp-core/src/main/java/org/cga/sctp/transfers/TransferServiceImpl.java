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

import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.beneficiaries.Household;
import org.cga.sctp.location.Location;
import org.cga.sctp.program.Program;
import org.cga.sctp.targeting.EnrollmentSessionView;
import org.cga.sctp.transfers.agencies.TransferAgenciesRepository;
import org.cga.sctp.transfers.epayments.TransferAccountNumberList;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.cga.sctp.transfers.reconciliation.TransferReconciliationRequest;
import org.cga.sctp.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private TransfersRepository transfersRepository;

    @Autowired
    private TransferAgenciesRepository transferAgenciesRepository;

    @Autowired
    private TransferSessionRepository transferSessionRepository;

    @Autowired
    private BeneficiaryService beneficiaryService;

    public TransferSessionRepository getTranferSessionRepository() {
        return transferSessionRepository;
    }

    @Override
    public TransferAgenciesRepository getTransferAgenciesRepository() {
        return transferAgenciesRepository;
    }

    @Override
    public TransfersRepository getTransfersRepository() {
        return transfersRepository;
    }

    @Transactional
    @Override
    public TransferSession initiateTransfers(Location location, TransferSession transferSession, long userId) {
        // TODO: refactor the initiation logic and use the other arguments provided
        Objects.requireNonNull(location);
        Objects.requireNonNull(transferSession);
        if (getTranferSessionRepository().save(transferSession) == null) {
            // TODO check
        }
        transfersRepository.initiateTransfersInDistrict(transferSession.getProgramId(), location.getId(), transferSession.getId(), userId);
        return transferSession;
    }

    @Override
    public Page<Transfer> fetchPendingTransferListByLocation(long districtCode, Long taCode, Long villageCluster, Long zone, Long village, Pageable pageable) {
        return transfersRepository.findAllByStatusByLocationToVillageLevel(
                TransferStatus.OPEN.getCode(),
                districtCode,
                taCode,
                villageCluster,
                zone,
                village,
                pageable
        );
    }

    @Override
    public void removeHouseholdFromTransfers(Household household, TransferPeriod transferPeriod, String reason) {
        throw new UnsupportedOperationException("not yet implemented"); // TODO: implement me
    }

    @Override
    public int reconcileTransfers(TransferReconciliationRequest transferReconciliationRequest) {
        throw new UnsupportedOperationException("not yet implemented"); // TODO: implement me
    }

    @Override
    public int updateTransferCalculations(TransferPeriod transferPeriod) {
        throw new UnsupportedOperationException("not yet implemented"); // TODO: implement me
    }

    @Override
    public int updatePerformedTransfers(TransferReconciliationRequest transferUpdates, long userId) {
        return 0;
    }

    @Override
    public int performManualTransfers(TransferReconciliationRequest transferUpdates, long userId) {
        // TODO: Verify each transfer is still 'Open' state
        // TODO: Verify each household for transfer is not 'Suspended'
        // TODO: Validate amount for the transfer
        // TODO: Calculate the arrears for each transfer
        // TODO:
        return 0;
    }

    @Override
    public int closeTransfers(List<Transfer> transferList, User user) {
        throw new UnsupportedOperationException("not yet implemented"); // TODO: implement me
    }

    @Override
    public int assignAccountNumbers(TransferSession session, TransferPeriod period, TransferAccountNumberList transferAccountNumberList) {
        throw new UnsupportedOperationException("not yet implemented"); // TODO: implement me
    }

    @Override
    public void exportTransferList(TransferPeriod transferPeriod, Path destinationPath) throws Exception {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public List<TransferSessionDetailView> findAllActiveSessions(Pageable pageable) {
        return transferSessionRepository.findAllActiveAsView(pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public List<TransferEventHouseholdView> findAllHouseholdsInSession(Long sessionId) {
        return transfersRepository.findAllHouseholdsByTransferSessionId(sessionId);
    }
}
