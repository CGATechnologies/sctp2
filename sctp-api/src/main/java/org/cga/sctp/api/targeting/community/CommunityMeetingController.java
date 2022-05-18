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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cga.sctp.api.core.BaseController;
import org.cga.sctp.api.core.IncludeGeneralResponses;
import org.cga.sctp.api.user.ApiUserDetails;
import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.targeting.CbtStatus;
import org.cga.sctp.targeting.EligibilityVerificationSessionView;
import org.cga.sctp.targeting.EligibleHouseholdDetails;
import org.cga.sctp.targeting.TargetingService;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/targeting/community-based-targeting")
@Tag(name = "CommunityMeeting", description = "Endpoint for managing second community and district meetings")
public class CommunityMeetingController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @GetMapping("/district")
    @Operation(description = "Fetches open sessions from the community based targeting that have households in the eligib")
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
        return ResponseEntity.ok(new PreEligibilityVerificationSessionResponse(verificationList));
    }

    @GetMapping("/sessions/{session-id}/households")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(anyOf = HouseholdDetailsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @IncludeGeneralResponses
    public ResponseEntity<HouseholdDetailsResponse> fetchHouseholdsBySessionId(
            @PathVariable("session-id") Long sessionId,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        EligibilityVerificationSessionView verificationSessionView = targetingService.findVerificationViewById(sessionId);

        if (verificationSessionView == null) {
            return ResponseEntity.notFound().build();
        }

        if (!verificationSessionView.isOpen()) {
            return ResponseEntity.badRequest().build();
        }

        Page<EligibleHouseholdDetails> households = targetingService.getEligibleHouseholdsDetails(
                verificationSessionView.getId(), page);

        List<HouseholdData> householdDataList =
                households.stream()
                        .map(HouseholdData::of).toList();

        return ResponseEntity.ok(new HouseholdDetailsResponse(
                households.getNumber(),
                households.getTotalElements(),
                households.getTotalPages(),
                householdDataList
        ));
        //return ResponseEntity.ok(new HouseholdDetailsResponse(households));
    }

    @PostMapping("/sessions/{session-id}/households/update-ranks")
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @IncludeGeneralResponses
    public ResponseEntity<?> updateHouseholdRank(@AuthenticatedUserDetails ApiUserDetails apiUserDetails,
                                                 @PathVariable("session-id") Long sessionId,
                                                 @Validated @RequestBody UpdateHouseholdRankRequest request) {

        EligibilityVerificationSessionView verificationSessionView = targetingService.findVerificationViewById(sessionId);
        if (verificationSessionView == null) {
            return ResponseEntity.badRequest().build();
        }

        if (request == null || !verificationSessionView.isOpen()) {
            return ResponseEntity.badRequest().build();
        }

        // FIXME: Make this operation run in a batch op instead of one-by-one like this
        request.getUpdates()
                .forEach(updateRankRequest -> {
                    publishGeneralEvent("User %s updated rank and status of household %s", apiUserDetails.getUserName(), updateRankRequest.getHouseholdId());
                    CbtStatus status = CbtStatus.valueOf(updateRankRequest.getCbtStatus());
                    beneficiaryService.updateHouseholdRankAndStatus(sessionId, updateRankRequest.getHouseholdId(), updateRankRequest.getRank(), status);
                });

        return ResponseEntity.ok().build();
    }
}
