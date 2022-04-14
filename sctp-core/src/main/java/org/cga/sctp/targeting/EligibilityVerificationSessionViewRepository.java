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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface EligibilityVerificationSessionViewRepository extends JpaRepository<EligibilityVerificationSessionView, Long> {

    @Query(
            nativeQuery = true,
            value = """
                    select * from eligibility_verification_sessions_v
                     where status = :status and taCode = :taCode and districtCode = :districtCode
                     and FIND_IN_SET(:clusterCode, clusters)
                    """
    )
    Page<EligibilityVerificationSessionView> findByOpenByLocation(
            Pageable pageable,
            @Param("status") String status,
            @Param("districtCode") long districtCode,
            @Param("taCode") Long taCode,
            @Param("clusterCode") Long villageClusterCode
    );

    @Query(
            nativeQuery = true,
            value = """
                    select * from eligibility_verification_sessions_v
                     where status = :status and taCode = :taCode and districtCode = :districtCode
                    """
    )
    Page<EligibilityVerificationSessionView> findByOpenByLocation(
            Pageable pageable,
            @Param("status") String status,
            @Param("districtCode") long districtCode,
            @Param("taCode") Long taCode
    );

    @Query(
            nativeQuery = true,
            value = """
                    select * from eligibility_verification_sessions_v
                     where status = :status and districtCode = :districtCode
                    """
    )
    Page<EligibilityVerificationSessionView> findByOpenByLocation(
            Pageable pageable,
            @Param("status") String status,
            @Param("districtCode") long districtCode
    );
}
