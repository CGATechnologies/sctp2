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
import org.cga.sctp.location.LocationService;
import org.cga.sctp.targeting.CbtStatus;
import org.cga.sctp.targeting.enrollment.EnrollmentService;
import org.cga.sctp.targeting.enrollment.HouseholdEnrollmentData;
import org.cga.sctp.transfers.accounts.BeneficiaryAccountService;
import org.cga.sctp.transfers.accounts.TransferAccountNumberList;
import org.cga.sctp.transfers.agencies.TransferAgenciesRepository;
import org.cga.sctp.transfers.parameters.*;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.cga.sctp.transfers.periods.TransferPeriodRepository;
import org.cga.sctp.transfers.reconciliation.TransferReconciliationRequest;
import org.cga.sctp.transfers.topups.TopUp;
import org.cga.sctp.transfers.topups.TopUpService;
import org.cga.sctp.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransferServiceImpl implements TransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceImpl.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransferPeriodRepository transferPeriodRepository;

    @Autowired
    private TransfersRepository transfersRepository;

    @Autowired
    private InitiateTransfersRepository initiateRepo;

    @Autowired
    private TransferAgenciesRepository transferAgenciesRepository;

    @Autowired
    private TransferSessionRepository transferSessionRepository;

    @Autowired
    private TopUpService topUpService;

    @Autowired
    private TransferParametersRepository transferParametersRepository;

    @Autowired
    private EducationTransferParameterRepository educationTransferParameterRepository;

    @Autowired
    private HouseholdTransferParametersRepository householdTransferParametersRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private BeneficiaryAccountService beneficiaryAccountService;

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
        Objects.requireNonNull(location);
        Objects.requireNonNull(transferSession);
        if (getTranferSessionRepository().save(transferSession) == null) {
            throw new IllegalArgumentException("transferSession must be valid to initiate transfers");
        }
        Optional<TransferPeriod> transferPeriod = transferPeriodRepository.findFirstByProgramIdAndDistrictIdAndIsOpen(transferSession.getProgramId(), location.getId());
        if (transferPeriod.isEmpty()) {
            throw new UnsupportedOperationException("Cannot initiate transfers without an open Transfer Period for the program in the given location");
        }
//
//        initiateRepo.initiateTransfersForEnrolledHouseholds(
//                // transferSession.getProgramId(),
//                transferSession.getEnrollmentSessionId(),
//                transferSession.getId(),
//                transferPeriod.get().getId(),
//                location.getId(),
//                userId
//        );
        // TODO: Mark household status as Beneficiary
        // TODO: Close the district for other operations
        return transferSession;
    }


    @Override
    public TransferSession createTransfers(TransferPeriod transferPeriod, long userId) {
        Optional<TransferPeriod> existingPeriod = transferPeriodRepository.findFirstByProgramIdAndDistrictIdAndIsOpen(transferPeriod.getProgramId(), transferPeriod.getDistrictCode());
        if (existingPeriod.isEmpty()) {
            throw new TransferException("Cannot initiate transfers without an open Transfer Period for the program in the given location");
        }

//        TransferSession transferSession = new TransferSession();
//        // TODO: Fetch latest Enrollment Session for the location in the period
//        transferSession.setEnrollmentSessionId();
//
//        if (getTranferSessionRepository().save(transferSession) == null) {
//            throw new IllegalArgumentException("transferSession must be valid to initiate transfers");
//        }
        Location location = locationService.findById(transferPeriod.getDistrictCode());

        var enrollmentSession = enrollmentService.findMostRecentSessionByLocation(location.getCode());
        if (enrollmentSession.isEmpty()) {
            // TODO:
            throw new TransferException("cannot initiate transfers in period when no enrollment sessions are available");
        }
        List<TopUp> topupsList = topUpService.findAllInLocation(location.getCode());
        Optional<TransferParameter> transferParameter =  transferParametersRepository.findByProgramId(transferPeriod.getProgramId());
        if (transferParameter.isEmpty()) {
            throw new TransferException("Program does not have an 'active' or valid transfer parameter");
        }
        List<EducationTransferParameter> programEducationParameters = educationTransferParameterRepository.findByTransferParameterId(transferParameter.get().getId());
        List<HouseholdTransferParameter> programHouseholdParameters = householdTransferParametersRepository.findByTransferParameterId(transferParameter.get().getId());
        TransferCalculator transferCalculator = new TransferCalculator(programHouseholdParameters, programEducationParameters, topupsList);
        List<Transfer> transferList = new ArrayList<>();

        transferCalculator.calculateTransfers(location, transferPeriod, transferList);
        initiateRepo.initiateTransfersForEnrolledHouseholds(
            enrollmentSession.get().getId(),
            transferPeriod.getId(),
            transferPeriod.getDistrictCode(),
            userId
        );

        return null;
    }

    // this implementation uses hibernate and pre-calculates the amounts for the transfers outside the db
    private void initiateTransfersForEnrolledHouseholds(TransferPeriod transferPeriod) {
        Location location = locationService.findById(transferPeriod.getDistrictCode());

        var enrollmentSession = enrollmentService.findMostRecentSessionByLocation(location.getCode());
        if (enrollmentSession.isEmpty()) {
            // TODO:
            throw new TransferException("cannot initiate transfers in period when no enrollment sessions are available");
        }
        long enrollmentSesionId = -1L; // TODO:
        Page<HouseholdEnrollmentData> householdEnrollmentData = enrollmentService.getHouseholdEnrollmentData(enrollmentSesionId, 1, 1000);

        householdEnrollmentData.map(enrollmentRecord -> {
            Transfer newTransfer = new Transfer();

            return newTransfer;
        });


    }

    @Override
    public List<Transfer> fetchTransferList(long districtCode, Long taCode, Long villageCluster, Long zone, Long village, Pageable pageable) {
        return transfersRepository.findAllByLocationToVillageLevel(
                districtCode,
                taCode,
                villageCluster,
                zone,
                village,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @Override
    public List<Transfer> fetchPendingTransferListByLocation(long districtCode, Long taCode, Long villageCluster, Long zone, Long village, Pageable pageable) {
        return transfersRepository.findAllByStatusByLocationToVillageLevel(
                TransferStatus.OPEN.getCode(),
                districtCode,
                taCode,
                villageCluster,
                zone,
                village,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );
    }

    @Override
    public void removeHouseholdFromTransfers(Household household, TransferPeriod transferPeriod, String reason) {
        throw new UnsupportedOperationException("not yet implemented"); // TODO: implement me
    }

    @Override
    public int reconcileTransfers(TransferReconciliationRequest transferReconciliationRequest) {
//        List<Transfer> transfers = transfersRepository.findAllByTransferPeriodId(transferReconciliationRequest.getTransferPeriodId());
        ZonedDateTime timestamp = ZonedDateTime.now();
        String sqlTemplate = """    
                UPDATE transfers t
                 JOIN household_enrollment eh ON eh.household_id = i.household_id
                 SET t.amount_collected = :amount_collected, i.modified_at = :timestamp
                 WHERE t.transfer_period_id = :transfer_period_id and t.household_id = :household_id
                ;""";

        int updated = 0;
        for (TransferReconciliationRequest.TransferReconciliation update : transferReconciliationRequest.getReconciliationList()) {
            entityManager.createNativeQuery(sqlTemplate)
                    .setParameter("timestamp", timestamp)
                    .setParameter("amount_collected", update.getAmountTransferred())
                    .setParameter("transfer_period_id", transferReconciliationRequest.getTransferPeriodId())
                    .setParameter("household_id", update.getHouseholdId())
                    .executeUpdate();
            updated++;
        }

        return updated;
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
        for (TransferReconciliationRequest.TransferReconciliation reconciliation : transferUpdates.getReconciliationList()) {
            Optional<Transfer> transferOptional = this.transfersRepository.findById(reconciliation.getTransferId());
            if (transferOptional.isEmpty()) {
                continue;
            }
            // TODO: Validate amount for the transfer
            Transfer transfer = transferOptional.get();
            Household household = beneficiaryService.findHouseholdById(transfer.getHouseholdId());

            if (household.getCbtStatus().equals(CbtStatus.NonRecertified)) {
                // TODO: last transfer they will ever receive, so lock them out somehow
            }
            // TODO: Verify each household for transfer is not 'Suspended'
            if (transfer.getTransferState().equals(TransferStatus.CLOSED)) {
                LOGGER.warn("Cannot update CLOSED Transfer transfer.id={}", transfer.getId());
                continue;
            }
            if (transfer.getHouseholdId() != reconciliation.getHouseholdId()) {
                LOGGER.warn("Transfer reconciliation has different household than database record. transfer.householdId={} reconciliation.householdId={}", transfer.getHouseholdId(), reconciliation.getHouseholdId());
                continue;
            }
            if (reconciliation.getAmountTransferred().doubleValue() < 0.0) {
                LOGGER.warn("Transfer reconciliation has invalid amount transfer.id={} reconciliation.amountTransferred={}", transfer.getId(), reconciliation.getAmountTransferred());
                continue;
            }

            transfer.setRecipientId(reconciliation.getRecipientId());
            transfer.setDisbursementDate(reconciliation.getTimestamp().toLocalDateTime());
            transfer.setAmountDisbursed(reconciliation.getAmountTransferred());
            transfer.setCollected(true);
            // TODO: Calculate the arrears for each transfer
            transfer.setArrearsAmount(transfer.getAmountDisbursed().subtract(transfer.getTotalAmountToTransfer()));
            // TODO: we need to have somewhere else to track arrears?
            transfer.setDisbursedByUserId(reconciliation.getReconcilingUserId());
        }
        return 0;
    }

    @Override
    public int closeTransfers(List<Transfer> transferList, User user) {
        int numUpdated = 0;
        for(Transfer transfer: transferList) {
            transfer.setCollected(true);
            transfer.setTransferState(TransferStatus.CLOSED);
            try {
                transfersRepository.save(transfer);
                numUpdated++;
            } catch (Exception e) {
                LOGGER.warn("Failed to update transfer {} got errror", transfer.getId(), e);
            }
        }
        return numUpdated;
    }

    @Override
    public int assignAccountNumbers(TransferSession session, TransferPeriod period, TransferAccountNumberList transferAccountNumberList) {
        // TODO: validate the session, check if the the period is open and check transfer agency for E-Payments which is the valid reason to assign account numbers
        // TODO: validate the session, check if the the period is open and check transfer agency for E-Payments which is the valid reason to assign account numbers
        List<Long> householdsAssigned = new ArrayList<>();
        for (TransferAccountNumberList.HouseholdAccountNumber hh : transferAccountNumberList.getAccountNumberList()) {
            transfersRepository.findByHouseholdId(hh.getHouseholdId())
                    .ifPresent(transfer -> {
                        // TODO: Check if the transfer period matches the period in the request
                        if (transfer.getTransferPeriodId() != period.getId()) {
                            transfer.setAccountNumber(hh.getAccountNumber());
                            transfer.setModifiedAt(ZonedDateTime.now());
                            transfersRepository.save(transfer);

                            householdsAssigned.add(hh.getHouseholdId());
                        }
                    });
        }
        LoggerFactory.getLogger(getClass()).info("Assigned {} account numbers", householdsAssigned.size());
        // TODO: return the actual list?
        return householdsAssigned.size();
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

    @Override
    public Optional<TransferSession> findLatestSessionInDistrict(Long districtId) {
        return getTranferSessionRepository().findOneByDistrictId(districtId);
    }

    @Override
    public List<DistrictTransferSummaryView> fetchDistrictSummaries() {
        // TODO: look at caching the results...
        return transferSessionRepository.fetchDistrictSummary();
    }

    @Override
    public int countUnreconciledTransfers(TransferPeriod transferPeriod) {
        if (transferPeriod == null) {
            return 0;
        }
        return transfersRepository.countOpenOrPreCloseTransfersInPeriod(transferPeriod.getTransferSessionId());
    }
}
