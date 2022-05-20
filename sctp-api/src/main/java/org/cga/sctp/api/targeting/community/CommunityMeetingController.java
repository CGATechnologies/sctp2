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
import org.cga.sctp.api.utils.LangUtils;
import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.targeting.TargetedHouseholdSummary;
import org.cga.sctp.targeting.TargetingService;
import org.cga.sctp.targeting.TargetingSessionView;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/targeting/meetings")
@Tag(name = "Second Community & District Meetings", description = "Endpoint for managing second community and district meetings")
public class CommunityMeetingController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @GetMapping("/second-community-meeting")
    @Operation(description = "Fetches open sessions from the community based targeting to be reviewed at the second community meeting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CommunityMeetingSessionResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<CommunityMeetingSessionResponse> getSessionsForSecondCommunityMeeting(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "traditional-authority-code", required = false, defaultValue = "0") Long taCode,
            @RequestParam(value = "village-cluster-code", required = false, defaultValue = "0") Long villageCluster,
            @RequestParam(value = "zone-code", required = false, defaultValue = "0") Long zone,
            @RequestParam(value = "village-code", required = false, defaultValue = "0") Long village) {

        Page<TargetingSessionView> sessions = targetingService
                .getOpenTargetingSessionsForSecondCommunityMeeting(
                        apiUserDetails.getAccessTokenClaims().getDistrictCode().longValue(),
                        LangUtils.nullIfZeroOrLess(taCode),
                        LangUtils.nullIfZeroOrLess(villageCluster),
                        page,
                        200
                );
        return ResponseEntity.ok(new CommunityMeetingSessionResponse(sessions));
    }

    @GetMapping("/district-meeting")
    @Operation(description = "Fetches open sessions from the community based targeting to be reviewed at the district meeting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CommunityMeetingSessionResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<CommunityMeetingSessionResponse> getSessionsForDistrictMeeting(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "traditional-authority-code", required = false, defaultValue = "0") Long taCode,
            @RequestParam(value = "village-cluster-code", required = false, defaultValue = "0") Long villageCluster,
            @RequestParam(value = "zone-code", required = false, defaultValue = "0") Long zone,
            @RequestParam(value = "village-code", required = false, defaultValue = "0") Long village) {

        Page<TargetingSessionView> sessions = targetingService
                .getOpenTargetingSessionsForDistrictMeeting(
                        apiUserDetails.getAccessTokenClaims().getDistrictCode().longValue(),
                        LangUtils.nullIfZeroOrLess(taCode),
                        LangUtils.nullIfZeroOrLess(villageCluster),
                        page,
                        200
                );
        return ResponseEntity.ok(new CommunityMeetingSessionResponse(sessions));
    }

    private ResponseEntity<TargetedHouseholdsResponse> getHouseholds(Long sessionId, int page, int pageSize, boolean scm) {

        TargetingSessionView sessionView = targetingService.findSessionViewById(sessionId);

        if (sessionView == null) {
            return ResponseEntity.notFound().build();
        }

        if (scm) {
            if (!sessionView.isAtSecondCommunityMeeting()) {
                return ResponseEntity.notFound().build();
            }
        } else {
            if (!sessionView.isAtDistrictMeeting()) {
                return ResponseEntity.notFound().build();
            }
        }

        Page<TargetedHouseholdSummary> summaries = targetingService.getTargetedHouseholdSummaries(
                sessionView.getId(),
                Pageable.ofSize(Math.max(pageSize, 500)).withPage(page)
        );

        return ResponseEntity.ok(new TargetedHouseholdsResponse(summaries));
    }

    @GetMapping("/second-community-meeting-households")
    @Operation(description = "Returns households under the given targeting session id. The session must be at the second community meeting stage.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TargetedHouseholdsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Targeting session not found. Session may not exist or be at the second community meeting stage.")
    })
    @IncludeGeneralResponses
    public ResponseEntity<TargetedHouseholdsResponse> getTargetedHouseholdsForSecondCommunityMeetingReview(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "targeting-session-id") Long sessionId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "500") int pageSize) {
        return getHouseholds(sessionId, page, pageSize, true);
    }

    @GetMapping("/district-meeting-households")
    @Operation(description = "Returns households under the given targeting session id. The session must be at the district meeting stage.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TargetedHouseholdsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Targeting session not found. Session may not exist or not be at the district meeting stage.")
    })
    @IncludeGeneralResponses
    public ResponseEntity<TargetedHouseholdsResponse> getTargetedHouseholdsForDistrictMeetingReview(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "targeting-session-id") Long sessionId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "500") int pageSize) {
        return getHouseholds(sessionId, page, pageSize, false);
    }
}
