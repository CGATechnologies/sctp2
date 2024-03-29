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

package org.cga.sctp.targeting.enrollment;

import org.cga.sctp.targeting.CbtStatus;
import org.cga.sctp.targeting.EligibilityVerificationSession;
import org.cga.sctp.targeting.TargetingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrolmentSessionRepository extends JpaRepository<EnrollmentSession, Long> {
    /**
     * Creates an enrollment session from {@link TargetingSession} or {@link EligibilityVerificationSession}
     *
     * @param targetingId    Targeting session id (0 or NUlL)
     * @param verificationId Verification session id (0 or NULL if )
     * @param userId         User id under which the enrollment session will be created
     */
    @Procedure(procedureName = "createEnrollmentSessionFromCbtOrPev")
    void createEnrollmentSessionFromCbtOrPev(
            @Param("targeting_session_id") Long targetingId,
            @Param("verification_session_id") Long verificationId,
            @Param("user_id") Long userId
    );

//    @Modifying
//    @Query(value = "UPDATE household_enrollment SET status = :statusCode WHERE household_id = :householdId", nativeQuery = true)
//    void updateHouseholdEnrollmentStatus(@Param("householdId") Long id, @Param("statusCode") int status);

    @Modifying
    @Query(value = "UPDATE household_enrollment SET status = :status WHERE session_id = :sessionId AND household_id = :householdId", nativeQuery = true)
    void updateHouseholdEnrollmentStatus(@Param("sessionId") Long sessionId, @Param("householdId") Long householdId, @Param("status") String status);

    @Query(value = "SELECT status FROM household_enrollment WHERE session_id = :sessionId AND household_id = :householdId", nativeQuery = true)
    String getHouseholdEnrollmentStatus(@Param("sessionId") Long sessionId, @Param("householdId") Long householdId);


    /**
     * Counts the households in the enrollment session with the given
     *
     * @param id            Enrollment Session ID
     * @param cbtStatusCode the status code
     * @return the number of households that match the criteria
     * @see CbtStatus
     */
    @Query(nativeQuery = true, value = "SELECT count(household_id) FROM household_enrollment WHERE session_id = :id AND status = :cbtStatusCode")
    Long countHouseholdsInSessionByStatus(@Param("id") Long id, @Param("cbtStatusCode") int cbtStatusCode);

    @Query(nativeQuery = true, value = "SELECT count(household_id) FROM household_enrollment WHERE session_id = :id AND (status = 6 OR status <> 5")
    int countPreEligibleOrNotEnrolled(@Param("id") Long id);

    @Query
    Optional<EnrollmentSession> findLastByDistrictCode(Long locationCode); // TODO: add isClosed parameter
}
