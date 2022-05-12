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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransferSessionRepository extends JpaRepository<TransferSession, Long> {
    @Query(nativeQuery = true, value = """
            SELECT 
              pg.name as programName,
              ts.* 
            FROM transfers_sessions ts
            INNER JOIN programs pg ON pg.id = ts.program_id
            LIMIT :page , :pageSize ;
            """)
    List<TransferSessionDetailView> findAllActiveAsView(@Param("page") int page, @Param("pageSize") int pageSize);

    @Query
    Optional<TransferSession> findOneByDistrictId(Long districtId);

    @Query(nativeQuery = true, value = """
            SELECT
              district.id as id,
              district.id as districtId,
              district.code as districtCode,
              district.name as districtName,
              district.created_at as created_at
            FROM locations AS district
            LEFT OUTER JOIN transfer_periods tp ON tp.district_id = district.id
            WHERE district.location_type = 'SUBNATIONAL1'
            ORDER BY district.name, tp.end_date
            """)
    List<DistrictTransferSummaryView> fetchDistrictSummary();
}
