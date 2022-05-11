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

import org.cga.sctp.beneficiaries.Household;
import org.cga.sctp.location.Location;
import org.cga.sctp.targeting.CbtStatus;
import org.cga.sctp.transfers.agencies.TransferAgenciesRepository;
import org.cga.sctp.transfers.accounts.TransferAccountNumberList;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.cga.sctp.transfers.reconciliation.TransferReconciliationRequest;
import org.cga.sctp.user.User;
import org.springframework.data.domain.Pageable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates and manages Transfers
 */
public interface TransferService {

    TransferSessionRepository getTranferSessionRepository();

    TransferAgenciesRepository getTransferAgenciesRepository();

    TransfersRepository getTransfersRepository();

    List<TransferSessionDetailView> findAllActiveSessions(Pageable pageable);

    List<TransferEventHouseholdView> findAllHouseholdsInSession(Long sessionId);

    /**
     * Initiates Transfers for the given program in the given location.
     * This results in all Households that are marked as {@link CbtStatus#Selected}
     * in the given location being graduated to {@link CbtStatus#Enrolled}
     * <br/>
     * Initiates Transfer Entries for households in the given session coming from a completed/closed enrollment.
     * The Transfer entries are created with default data for amounts, etc...
     *
     * @param location the location transfers will be performed in
     * @param transferSession transferSession
     * @param userId user who performed activity
     */
    TransferSession initiateTransfers(Location location, TransferSession transferSession, long userId);

    List<Transfer> fetchPendingTransferListByLocation(long districtCode, Long taCode, Long villageCluster, Long zone, Long village, Pageable pageable);

    /**
     * Removes a household from transfer with given reason
     * @param household houshold to remove
     * @param transferPeriod optional? transfer period
     * @param reason the reason they are being removed
     */
    void removeHouseholdFromTransfers(Household household, TransferPeriod transferPeriod, String reason);

    /**
     * Update transfer calculations for all transfers in a period
     * @param transferPeriod the period to calculate and update for
     * @return number of transfer entries updated
     */
    int updateTransferCalculations(TransferPeriod transferPeriod);

    /**
     * Reconciles transfers by updating them with the actual amounts required, but doesn't close transfers
     * @param transferReconciliationRequest reconciliation request
     * @return number of transfers affected
     */
    int reconcileTransfers(TransferReconciliationRequest transferReconciliationRequest);

    /**
     * Updates the transfers after they have been performed, this can only be done once for each Transfer.
     *
     * @param transferUpdates transfers to update
     * @param userId user who requested/initiate the update
     * @return
     */
    int updatePerformedTransfers(TransferReconciliationRequest transferUpdates, long userId);
    
    /**
     * Performs manual transfers - which is basically updating the amounts.
     *
     * @param transferUpdates transfers to update
     * @param userId user who requested/initiate the update
     * @see #updatePerformedTransfers(TransferReconciliationRequest, long)
     * @return
     */
    int performManualTransfers(TransferReconciliationRequest transferUpdates, long userId);

    /**
     * Mark the provided transfers as closed
     * @param transferList list of transfers
     * @param user user who performed the operation
     * @return number of transfers successfully closed
     */
    int closeTransfers(List<Transfer> transferList, User user);


    /**
     * Assign account numbers to transfers in a given TransferSession and TransferPeriod
     * @param transferAccountNumberList
     * @return
     */
    int assignAccountNumbers(TransferSession session, TransferPeriod period, TransferAccountNumberList transferAccountNumberList);

    /**
     * Exports transfers list to the given directory
     * @param transferPeriod transfer period to export for
     * @param destinationPath path to export to
     * @throws Exception any error that occurs during the process
     */
    void exportTransferList(TransferPeriod transferPeriod, Path destinationPath) throws Exception;

    List<Transfer> fetchTransferList(long districtCode, Long taCode, Long villageCluster, Long zone, Long village, Pageable pageable);

    Optional<TransferSession> findLatestSessionInDistrict(Long districtId);

    /**
     * Fetches District Transfer Summary information from the database.
     * WARNING this may be a costly operation and therefore we need to cache the results of this call as it has to
     * do potentially a lot of compute for the amounts.
     * // TODO: check that there is caching in implementation
     * @return list of district summaries
     */
    List<DistrictTransferSummaryView> fetchDistrictSummaries();
}
