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

package org.cga.sctp.transfers.periods;

import org.cga.sctp.transfers.TransfersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferPeriodService {
    @Autowired
    private TransferPeriodRepository transferPeriodRepository;

    @Autowired
    private TransfersRepository transfersRepository;

    /**
     * Creates a new transfer period if possible.
     *
     * The following preconditions must be met:
     *
     * * Transfer Agency must be assigned to the location the period is being created
     * * Previously opened periods must be closed before this one can be created.
     * * The Period must have start date after the end date of the previous period
     *
     * @param newPeriod
     * @return
     */
    public TransferPeriod openNewPeriod(TransferPeriod newPeriod) {
        // Check if the transfer period is valid (start to end dates)
        if (newPeriod.getStartDate().isBefore(newPeriod.getEndDate())) {
            // TODO: throw an exception here
            return null;
        }
        // Check if we don't have another period already running
        // Check if the program is allowed to have a new period
        if (transferPeriodRepository.countAllOpenInProgram(newPeriod.getProgramId()) > 0L) {
            // TODO: throw an exception here
            return null;
        }

        // Check if the households
        return transferPeriodRepository.save(newPeriod);
    }

    public void closePeriod(TransferPeriod transferPeriod) {
        // TODO: attempt to close the transfer period here...
        int pendingTransfers  = 0;// TODO: transfersRepository.countUnreconciledTransfers();
        if (pendingTransfers > 0) {
            throw new UnsupportedOperationException("cannot close period when transfers haven't been closed/reconciled");
        }
    }

    public List<TransferPeriod> findAll() {
        return transferPeriodRepository.findAll();
    }

    public List<TransferPeriod> findAllOpen() {
        // TODO: implement me
        throw new UnsupportedOperationException("not yet implemented");
    }

    public List<TransferPeriod> findAllByDistrictId(Long districtId) {
        return transferPeriodRepository.findAllByDistrictId(districtId);
    }
}
