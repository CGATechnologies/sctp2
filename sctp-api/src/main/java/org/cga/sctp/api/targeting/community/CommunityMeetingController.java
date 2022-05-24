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
import org.cga.sctp.audit.TargetingEvent;
import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.targeting.TargetedHouseholdSummary;
import org.cga.sctp.targeting.TargetingService;
import org.cga.sctp.targeting.TargetingSession;
import org.cga.sctp.targeting.TargetingSessionView;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.OffsetDateTime;

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
            @Valid @Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @Min(100) @Max(1000) @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
            @RequestParam(value = "traditional-authority-code", required = false) Long taCode,
            @RequestParam(value = "village-cluster-code", required = false) Long villageCluster,
            @RequestParam(value = "zone-code", required = false) Long zone,
            @RequestParam(value = "village-code", required = false) Long village) {

        Page<TargetingSessionView> sessions = targetingService
                .getOpenTargetingSessionsForSecondCommunityMeeting(
                        apiUserDetails.getAccessTokenClaims().getDistrictCode().longValue(),
                        LangUtils.nullIfZeroOrLess(taCode),
                        LangUtils.nullIfZeroOrLess(villageCluster),
                        page,
                        pageSize
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
            @Valid @Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @Min(100) @Max(1000) @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
            @RequestParam(value = "traditional-authority-code", required = false) Long taCode,
            @RequestParam(value = "village-cluster-code", required = false) Long villageCluster,
            @RequestParam(value = "zone-code", required = false) Long zone,
            @RequestParam(value = "village-code", required = false) Long village) {

        Page<TargetingSessionView> sessions = targetingService
                .getOpenTargetingSessionsForDistrictMeeting(
                        apiUserDetails.getAccessTokenClaims().getDistrictCode().longValue(),
                        LangUtils.nullIfZeroOrLess(taCode),
                        LangUtils.nullIfZeroOrLess(villageCluster),
                        page,
                        pageSize
                );
        return ResponseEntity.ok(new CommunityMeetingSessionResponse(sessions));
    }

    private ResponseEntity<TargetedHouseholdsResponse> getHouseholds(Long sessionId, int page, int pageSize, boolean scm) {

        TargetingSessionView sessionView = targetingService.findTargetingSessionViewById(sessionId);

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
                PageRequest.of(page - 1, pageSize)
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
            @Valid @Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @Min(100) @Max(1000) @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize) {
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
            @Valid @Min(0) @RequestParam(value = "page", defaultValue = "0") int page,
            @Valid @Min(100) @Max(1000) @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize) {
        return getHouseholds(sessionId, page, pageSize, false);
    }

    @PostMapping("/second-community-meeting-households-update")
    @Operation(description = "Returns households under the given targeting session id. The session must be at the second community meeting stage.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Targeting session not found. Session may not exist or be at the second community meeting stage.")
    })
    @IncludeGeneralResponses
    public ResponseEntity<?> updateSecondCommunityMeetingHouseholds(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "targeting-session-id") Long sessionId,
            @Valid @RequestBody TargetedHouseholdUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        TargetingSessionView session = targetingService.findTargetingSessionViewById(sessionId);

        if (session == null || !session.isAppCommunityMeeting()) {
            return ResponseEntity.notFound().build();
        }

        if (request.getStatuses().isEmpty()) {
            LOG.warn("Attempt to update with empty data");
            return ResponseEntity.badRequest().build();
        }

        if (!targetingService.updateTargetedHouseholds(session, request.getStatuses(), apiUserDetails.getUserId())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        TargetingEvent.Builder b = TargetingEvent.builder();
        publishEvent(TargetingEvent.builder()
                .message("%s updated targeted %,d households under session %d")
                .field(apiUserDetails.getUserName())
                .field(request.getStatuses().toString())
                .field(session.getId().toString())
                .build()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/second-community-meeting-done")
    @Operation(description = "Marks this session as having gone past the second community meeting stage. Second community meetings can no longer be done after this.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Targeting session not found. Session may not exist or be at the second community meeting stage.")
    })
    @IncludeGeneralResponses
    public ResponseEntity<?> markCommunityMeetingAsDone(
            @AuthenticatedUserDetails ApiUserDetails apiUserDetails,
            @RequestParam(value = "targeting-session-id") Long sessionId,
            @Valid @RequestBody TargetedHouseholdUpdateRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        TargetingSession session = targetingService.findTargetingSessionById(sessionId);

        if (session == null || !session.isAppCommunityMeeting()) {
            return ResponseEntity.notFound().build();
        }

        session.setAppCommunityMeeting(true);
        session.setCommunityMeetingTimestamp(OffsetDateTime.now());
        session.setCommunityMeetingUserId(apiUserDetails.getUserId());

        targetingService.saveTargetingSession(session);

        publishEvent(TargetingEvent.builder()
                .message("%s closed second community targeting for session with %d.")
                .field(apiUserDetails.getUserName())
                .field(session.getId().toString())
                .build()
        );

        return ResponseEntity.ok().build();
    }
}
