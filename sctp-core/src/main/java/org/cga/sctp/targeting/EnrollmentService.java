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

package org.cga.sctp.targeting;

import org.cga.sctp.core.TransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentService extends TransactionalService {

    @Autowired
    CbtRankingRepository cbtRankingRepository;

    @Autowired
    EnrolmentSessionRepository enrolmentSessionRepository;

    @Autowired
    EnrollmentSessionViewRepository sessionViewRepository;

    @Autowired
    EnrollmentHouseholdRepository enrollmentHouseholdRepository;

    @Autowired
    AlternateRecipientRepository alternateRecipientRepository;

    @Autowired
    HouseholdRecipientRepository householdRecipientRepository;

    @Autowired
    SchoolEnrolledRepository schoolEnrolledRepository;


    public List<EnrollmentSessionView> getEnrollmentSessions() {
        return sessionViewRepository.findAll();
    }

    public Slice<CbtRanking> getEnrolledHouseholds(EnrollmentSessionView session, Pageable pageable) {
        return cbtRankingRepository.findByCbtSessionId(session.getId(), pageable);
    }

    public EnrollmentSessionView getEnrollmentSession(Long sessionId) {
        return sessionViewRepository.findById(sessionId).orElse(null);
    }

    public EnrollmentHousehold findEnrollmentHousehold(Long sessionId, long householdId) {
        return enrollmentHouseholdRepository.findBySessionAndHousehold(sessionId, householdId);
    }

    public HouseholdDetails getHouseholdDetails(Long householdId) {
        return enrollmentHouseholdRepository.getEnrolledHouseholdDetails(householdId);
    }

    public void saveAlternateRecipient(AlternateRecipient alternateRecipient) {
        alternateRecipientRepository.save(alternateRecipient);
    }

    public void saveHouseholdRecipient(HouseholdRecipient householdRecipient) {
        householdRecipientRepository.save(householdRecipient);
    }

    public void saveHouseholdAlternateRecipient(Long householdId,
                                                Long mainRecipientId,
                                                String mainPhoto,
                                                String altPhoto,
                                                String firstName,
                                                String lastName,
                                                String nationalId,
                                                int gender,
                                                LocalDate dob) {
        householdRecipientRepository.addHouseholdRecipient(householdId, mainRecipientId, mainPhoto, altPhoto, firstName, lastName, nationalId, gender, dob);
    }

    public void saveChildrenEnrolledSchool(List<SchoolEnrolled> schoolEnrolled) {
        schoolEnrolledRepository.saveAll(schoolEnrolled);
    }

    public HouseholdRecipient getHouseholdRecipient(Long householdId) {
        return householdRecipientRepository.findById(householdId).orElse(null);
    }

    public AlternateRecipient getHouseholdAlternateRecipient(Long alternateId) {
        return alternateRecipientRepository.findById(alternateId).orElse(null);
    }

    public List<SchoolEnrolled> getSchoolEnrolledByHousehold(Long householdId) {
        return schoolEnrolledRepository.findHouseholdSchoolEnrolled(householdId);
    }

    public void setEnrollmentHouseholdEnrolled(Long householdId) {
        enrolmentSessionRepository.setEnrolledHouseholdToEnrolled(householdId);
    }

    public void updateHouseholdEnrollmentStatus(Long householdId, CbtStatus status) {
        enrolmentSessionRepository.updateHouseholdEnrollmentStatus(householdId, status.code);
    }


    public boolean sessionHasPendingHouseholdsToEnroll(Long enrollmentSessionId) {
        return enrolmentSessionRepository.countUnenrolledHouseholds(enrollmentSessionId) > 0;
    }
}
