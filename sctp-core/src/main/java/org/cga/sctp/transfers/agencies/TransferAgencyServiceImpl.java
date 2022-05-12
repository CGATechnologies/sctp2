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

package org.cga.sctp.transfers.agencies;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransferAgencyServiceImpl implements TransferAgencyService {

    @Value(value = "${sctp.transfers.manualTransferFrequency}")
    private int manualTransferFrequency = 2;
    
    private int ePaymentTransferFrequency = 1;

    @Autowired
    private TransferAgenciesRepository transferAgenciesRepository;

    @Autowired
    private TransferAgencyAssignmentRepository transferAgencyAssignmentRepository;

    @Autowired
    private LocationService locationService;

    public TransferAgenciesRepository getTransferAgenciesRepository() {
        return transferAgenciesRepository;
    }

    public List<TransferAgency> fetchAllTransferAgencies() {
        return transferAgenciesRepository.findAll();
    }

    public TransferAgency findActiveTransferAgencyById(Long transferAgencyId) {
        return transferAgenciesRepository.getOne(transferAgencyId);
    }

    @Override
    public void save(TransferAgency transferAgency) {
        transferAgenciesRepository.save(transferAgency);
    }

    /**
     * Assign a Transfer Agency to the given Geolocation area.
     * @param transferAgency the agency to assign to the location
     * @param location the geolocation area
     * @param transferMethod the method the agency will use for transfers in the location
     * @param assignedBy user who assigned the agency
     * @return transfer agency assignment entity
     */
    public TransferAgencyAssignment assignAgency(TransferAgency transferAgency,
                                                 Location location,
                                                 TransferMethod transferMethod,
                                                 Long assignedBy) throws TransferAgencyAlreadyAssignedException {

        if (transferAgencyAssignmentRepository.locationOrParentHasAssignedAgency(location.getCode()) > 0) {
            throw new TransferAgencyAlreadyAssignedException("Cannot assign transfer agency to location because it already has been assigned");
        }

        TransferAgencyAssignment agencyAssignment = new TransferAgencyAssignment();
        agencyAssignment.setTransferAgencyId(transferAgency.getId());
        agencyAssignment.setLocationId(location.getId());
        agencyAssignment.setTransferMethod(transferMethod);
        agencyAssignment.setAssignedBy(assignedBy);

        if (agencyAssignment.getTransferMethod().equals(TransferMethod.Manual)) {
            agencyAssignment.setFrequency(manualTransferFrequency);
        } else if (agencyAssignment.getTransferMethod().equals(TransferMethod.EPayment)) {
            agencyAssignment.setFrequency(ePaymentTransferFrequency);
        }

        agencyAssignment.setCreatedAt(LocalDateTime.now());
        agencyAssignment.setModifiedAt(agencyAssignment.getCreatedAt());

        return transferAgencyAssignmentRepository.save(agencyAssignment);
    }

    @Override
    public Optional<TransferAgency> findById(Long transferAgencyId) {
        return transferAgenciesRepository.findById(transferAgencyId);
    }

    @Override
    public List<TransferAgency> findAllByTransferModality(String transferMethod) {
        return transferAgenciesRepository.findAllByTransferMethod(TransferMethod.valueOf(transferMethod));
    }
}
