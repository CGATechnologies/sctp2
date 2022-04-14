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

package org.cga.sctp.api.targeting.community;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.cga.sctp.api.core.BaseController;
import org.cga.sctp.api.core.IncludeGeneralResponses;
import org.cga.sctp.api.households.HouseholdDetailResponse;
import org.cga.sctp.api.user.ApiUserDetails;
import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.targeting.EligibilityVerificationSessionView;
import org.cga.sctp.targeting.EligibleHousehold;
import org.cga.sctp.targeting.TargetingService;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/targeting/pre-eligibility")
public class PreEligibilityVerificationsController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @GetMapping("/sessions")
    @Operation(description = "Fetches open pre-eligibility sessions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PreEligibilityVerificationSessionResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<PreEligibilityVerificationSessionResponse> getSessions(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "traditional-authority-code", required = false, defaultValue = "0") Long taCode,
            @RequestParam(value = "village-cluster-code", required = false, defaultValue = "0") Long villageCluster,
            @RequestParam(value = "zone-code", required = false, defaultValue = "0") Long zone,
            @RequestParam(value = "village-code", required = false, defaultValue = "0") Long village) {

        Page<EligibilityVerificationSessionView> verificationList
                = targetingService.getOpenVerificationSessionsByLocation(
                apiUserDetails.getAccessTokenClaims().getDistrictCode().longValue(),
                taCode,
                villageCluster,
                zone,
                village,
                page
        );
        return ResponseEntity.ok(new PreEligibilityVerificationSessionResponse(
                page,
                verificationList.getTotalPages(),
                verificationList.toList()
        ));
    }

    @GetMapping("/sessions/{session-id}/households")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(anyOf = HouseholdDetailResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<List<HouseholdDetailResponse>> fetchHouseholdsById(@PathVariable("session-id") Long sessionId) {
        EligibilityVerificationSessionView verificationSessionView = targetingService.findVerificationViewById(sessionId);
        if (verificationSessionView == null) {
            return ResponseEntity.badRequest().build();
        }
        List<EligibleHousehold> households = targetingService.getEligibleHouseholds(verificationSessionView);

        List<HouseholdDetailResponse> householdDetailResponses = new ArrayList<>();
        households.forEach(eligibleHousehold -> {
            HouseholdDetailResponse hh = new HouseholdDetailResponse();
            hh.setHousehold(eligibleHousehold);
            hh.setMembers(beneficiaryService.getHouseholdMembers(hh.getHousehold().getHouseholdId()));

            householdDetailResponses.add(hh);
        });

        return ResponseEntity.ok(householdDetailResponses);
    }
}
