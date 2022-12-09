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

public interface TransfersRepository extends JpaRepository<Transfer, Long> {

    @Query(nativeQuery = true, value = """
            SELECT
                 h.household_id householdId
                ,h.ubr_code formNumber
                ,l.name districtName
                ,l2.name taName
                ,l3.name zoneName
                ,l4.name clusterName
                ,l5.name villageName
                ,h.ml_code mlCode
                ,h.group_village_head_name villageHeadName
                ,CONCAT(i.first_name, ' ', i.last_name) householdHead
                ,te.household_member_count as memberCount
                ,te.children_count as childrenCount
                ,te.primary_children_count as primaryChildren
                ,te.secondary_children_count as secondaryChildren
                ,CONCAT(i2.first_name, ' ', i2.last_name) as receiverName
                ,te.primary_incentive_amount as primaryIncentive
                ,te.secondary_incentive_amount as secondaryIncentive
                ,te.basic_subsidy_amount as monthlyAmount
                ,te.number_of_months as numberOfMonths
                ,te.total_transfer_amount as totalMonthlyAmount
                ,te.arrears_amount as totalArrears
                ,te.total_transfer_amount as totalAmount
                ,te.is_first_transfer as isFirstTransfer
            FROM households h
            INNER JOIN transfers te ON h.household_id = te.household_id
            LEFT JOIN locations l ON l.code = h.location_code
            LEFT JOIN locations l2 ON l2.code = h.ta_code
            LEFT JOIN locations l3 ON l3.code = h.zone_code
            LEFT JOIN locations l4 ON l4.code = h.cluster_code
            LEFT JOIN locations l5 ON l5.code = h.village_code
            LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1 
            LEFT JOIN individuals i2 ON i2.id = te.receiver_id 
            WHERE te.transfer_session_id = :sessionId
            """)
    List<TransferEventHouseholdView> findAllHouseholdsByTransferSessionId(@Param("sessionId") Long id);

    @Query(nativeQuery = true, value = """
            SELECT t.*
            FROM transfers t
            INNER JOIN households h ON h.household_id = t.household_id
            LEFT JOIN locations l ON l.code = h.location_code
            LEFT JOIN locations l2 ON l2.code = h.ta_code
            LEFT JOIN locations l3 ON l3.code = h.zone_code
            LEFT JOIN locations l4 ON l4.code = h.cluster_code
            LEFT JOIN locations l5 ON l5.code = h.village_code
            LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
            LEFT JOIN individuals i2 ON i2.id = t.receiver_id
            ;
            """)
    /*
                WHERE t.status = :transferStatusCode
              AND (  l.code = :districtCode
                AND l2.code = :taCode
                AND l3.code = :clusterCode
                AND l4.code = :zoneCode
                AND l5.code = :villageCode )
              LIMIT :pageNumber, :pageSize
     */
    List<Transfer> findAllByStatusByLocationToVillageLevel(@Param("transferStatusCode") int transferStatusCode,
                                                           @Param("districtCode") long districtCode,
                                                           @Param("taCode") Long taCode,
                                                           @Param("clusterCode") Long clusterCode,
                                                           @Param("zoneCode") Long zoneCode,
                                                           @Param("villageCode") Long villageCode,
                                                           @Param("pageNumber") int pageNumber,
                                                           @Param("pageSize") int pageSize);


    @Query(nativeQuery = true, value = """
            SELECT t.*
            FROM transfers t
            INNER JOIN households h ON h.household_id = t.household_id
            LEFT JOIN locations l ON l.code = h.location_code
            LEFT JOIN locations l2 ON l2.code = h.ta_code
            LEFT JOIN locations l3 ON l3.code = h.zone_code
            LEFT JOIN locations l4 ON l4.code = h.cluster_code
            LEFT JOIN locations l5 ON l5.code = h.village_code
            LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
            LEFT JOIN individuals i2 ON i2.id = t.receiver_id
            WHERE (  l.code = :districtCode
                  AND l2.code = :taCode
                  AND l3.code = :clusterCode
                  AND l4.code = :zoneCode
                  AND l5.code = :villageCode )
                LIMIT :pageNumber, :pageSize ;
            """)
    List<Transfer> findAllByLocationToVillageLevel(@Param("districtCode") long districtCode,
                                                   @Param("taCode") Long taCode,
                                                   @Param("clusterCode") Long clusterCode,
                                                   @Param("zoneCode") Long zoneCode,
                                                   @Param("villageCode") Long villageCode,
                                                   @Param("pageNumber") int pageNumber,
                                                   @Param("pageSize") int pageSize);

    /**
     * @param periodId
     * @param districtCode
     * @param taCode
     * @param clusterCode
     * @param zoneCode
     * @param villageCode
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Query(nativeQuery = true, value = """
            SELECT t.*
            FROM transfers t
            INNER JOIN households h ON h.household_id = t.household_id
            LEFT JOIN locations l ON l.code = h.location_code
            LEFT JOIN locations l2 ON l2.code = h.ta_code
            LEFT JOIN locations l3 ON l3.code = h.zone_code
            LEFT JOIN locations l4 ON l4.code = h.cluster_code
            LEFT JOIN locations l5 ON l5.code = h.village_code
            LEFT JOIN individuals i ON i.household_id = h.household_id AND i.relationship_to_head = 1
            LEFT JOIN individuals i2 ON i2.id = t.receiver_id
            WHERE ( t.transfer_period_id = :periodId
                  AND l.code = :districtCode
                  AND l2.code = :taCode
                  AND l3.code = :clusterCode
                  AND l4.code = :zoneCode
                  AND l5.code = :villageCode )
                LIMIT :pageNumber, :pageSize ;
            """)
    List<Transfer> findAllByPeriodByLocationToVillageLevel(
                                                    @Param("periodId") long periodId,
                                                    @Param("districtCode") long districtCode,
                                                    @Param("taCode") Long taCode,
                                                    @Param("clusterCode") Long clusterCode,
                                                    @Param("zoneCode") Long zoneCode,
                                                    @Param("villageCode") Long villageCode,
                                                    @Param("pageNumber") int pageNumber,
                                                    @Param("pageSize") int pageSize);

    @Query
    Optional<Transfer> findByHouseholdId(Long householdId);
}
