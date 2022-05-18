/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
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

package org.cga.sctp.beneficiaries;

import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.CbtStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficiaryService extends TransactionalService {

    @Autowired
    private IndividualRepository individualRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    public DashboardStats getDashboardStats() {
        return individualRepository.getDashboardStats();
    }

    public void saveHousehold(Household household) {
        householdRepository.save(household);
    }

    public Page<Household> findAllHouseholdsPaged(Pageable pageable) {
        return householdRepository.findAll(pageable);
    }

    public Household findHouseholdByTargetingSessionIdAndHouseholdId(Long cbtSessionId, Long household) {
        return householdRepository.findByCbtSessionIdAndHouseholdId(cbtSessionId, household);
    }

    public Slice<Individual> getIndividualsForCommunityReview(Long householdId, Pageable pageable) {
        return individualRepository.findByHouseholdId(householdId, pageable);
    }

    public Individual getIndividual(Long individualId) {
        return individualRepository.findById(individualId).orElse(null);
    }

    public List<Individual> findSchoolChildren(Long householdId) {
        return individualRepository.findSchoolChildren(householdId);
    }

    public List<Individual> getEligibleRecipients(Long householdId) {
        return individualRepository.getEligibleRecipients(householdId);
    }

    public Household findHouseholdById(Long householdId) {
        return householdRepository.findById(householdId).orElse(null);
    }

    public List<Individual> getHouseholdMembers(Long householdId) {
        return individualRepository.findByHouseholdId(householdId);
    }

    public void updateHouseholdRankAndStatus(Long sessionId, Long householdId, Long rank, CbtStatus status) {
        householdRepository.updateHouseholdRankAndStatus(sessionId, householdId, rank, status.name());
    }

    public Optional<Household> findHouseholdByMLCode(@NonNull final String mlCode) {
        return householdRepository.findOneByMlCode(mlCode);
    }

    public List<Household> findAllHouseholdsByDistrictPaged(Long locationCode, Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.ofSize(100);
        }
        return householdRepository.findAllByLocationCode(String.valueOf(locationCode));//, pageable);
    }
}
