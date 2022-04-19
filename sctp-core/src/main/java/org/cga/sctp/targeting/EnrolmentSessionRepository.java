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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrolmentSessionRepository extends JpaRepository<EnrolmentSession, Long> {
    @Procedure(procedureName = "sendHouseholdToEnrolment")
    void sendToEnrolment(
            @Param("targeting_session_id") Long targetingId,
            @Param("verification_session_id") Long verificationId,
            @Param("user_id") Long userId
    );

    @Modifying
    @Query(value = "UPDATE household_enrollment SET status = 4 WHERE household_id = :id", nativeQuery = true)
    void setEnrolledHouseholdToEnrolled(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE household_enrollment SET status = :statusCode WHERE household_id = :householdId", nativeQuery = true)
    void updateHouseholdEnrollmentStatus(@Param("householdId") Long id, @Param("statusCode") int status);

    /**
     * Counts the households in the enrollment session that have not yet been moved to enrolled status (i.e. are
     * not yet Beneficiaries)
     *
     * @param id Enrollment Session ID
     * @return the number of households that have not been enrolled
     * @see CbtStatus
     */
    @Query(nativeQuery=true, value="SELECT count(household_id) FROM household_enrollment WHERE session_id = :id AND status = 1")
    Long countUnenrolledHouseholds(@Param("id") Long id);
}
