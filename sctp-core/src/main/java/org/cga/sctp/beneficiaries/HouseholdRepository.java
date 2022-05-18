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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface HouseholdRepository extends JpaRepository<Household, Long> {

    @Query(value = "select * from targeted_households_view WHERE household_id = :hhid AND targeting_session = :tsid", nativeQuery = true)
    Household findByCbtSessionIdAndHouseholdId(@Param("tsid") Long cbtSessionId, @Param("hhid") Long household);

    Optional<Household> findOneByMlCode(String mlCode);

    @Modifying
    @Query(value = "UPDATE targeting_results SET status = :status, ranking = :rank WHERE household_id = :householdId AND targeting_session = :tsid", nativeQuery = true)
    void updateHouseholdRankAndStatus(
            @Param("tsid") Long sessionId
            , @Param("householdId") Long id
            , @Param("rank") Long rank
            , @Param("status") String status
    );

//    @Query(nativeQuery = true, value = """
//            SELECT `h`.`household_id` AS `household_id`,`h`.`ubr_code` AS `form_number`,`l`.`name` AS `district_name`,`l2`.`name` AS `ta_name`,`l3`.`name` AS `zone_name`,`l4`.`name` AS `cluster_name`,`l5`.`name` AS `village_name`, CONCAT(`i`.`first_name`,' ',`i`.`last_name`) AS `household_head`,`h`.`cbt_rank` AS `rank`,`h`.`cbt_session_id` AS `cbt_session_id`,`h`.`cbt_selection` AS `cbt_selection`,`h`.`cbt_pmt` AS `pmt_score`,`h`.`cbt_status` AS `status`,`h`.`last_cbt_ranking` AS `last_ranking`,(
//            SELECT COUNT(`i2`.`id`)
//            FROM `individuals` `i2`
//            WHERE (`i2`.`household_id` = `h`.`household_id`)) AS `member_count`,`h`.`ml_code` AS `ml_code`,`h`.`group_village_head_name` AS `village_head_name`
//            FROM ((((((`households` `h`
//            LEFT JOIN `locations` `l` ON((`l`.`code` = `h`.`location_code`)))
//            LEFT JOIN `locations` `l2` ON((`l2`.`code` = `h`.`ta_code`)))
//            LEFT JOIN `locations` `l3` ON((`l3`.`code` = `h`.`zone_code`)))
//            LEFT JOIN `locations` `l4` ON((`l4`.`code` = `h`.`cluster_code`)))
//            LEFT JOIN `locations` `l5` ON((`l5`.`code` = `h`.`village_code`)))
//            LEFT JOIN `individuals` `i` ON(((`i`.`household_id` = `h`.`household_id`) AND (`i`.`relationship_to_head` = 1))))
//            WHERE l.code = :locationCode
//            ORDER BY h.household_id
//    """)
    @Query
    List<Household> findAllByLocationCode(@Param("locationCode") String locationCode);
}
