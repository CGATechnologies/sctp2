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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface TargetingSessionViewRepository extends JpaRepository<TargetingSessionView, Long> {

    @Procedure(procedureName = "getTargetingSessionsByLocation")
    List<TargetingSessionView> getTargetingSessionsByLocation(
            @Param("district") long districtCode,
            @Param("ta") Long taCode,
            @Param("cluster") Long clusterCode,
            @Param("page") int page,
            @Param("pageSize") int pageSize,
            @Param("status") String status,
            @Param("meetingPhase") String meetingPhase
    );

    @Query(
            value = """
                    CALL countTargetingSessionsByLocation(
                        :_districtCode
                       ,:_taCode
                       ,:_clusterCode
                       ,:_statusHint
                       ,:_meetingPhase)
                    """
            , nativeQuery = true
    )
    Long countTargetingSessionsByLocation(
            @Param("_districtCode") long districtCode,
            @Param("_taCode") Long taCode,
            @Param("_clusterCode") Long clusterCode,
            @Param("_statusHint") String status,
            @Param("_meetingPhase") String meetingPhase
    );
}
