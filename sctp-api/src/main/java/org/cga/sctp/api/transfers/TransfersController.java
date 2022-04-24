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

package org.cga.sctp.api.transfers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.cga.sctp.api.core.IncludeGeneralResponses;
import org.cga.sctp.api.user.ApiUserDetails;
import org.cga.sctp.transfers.Transfer;
import org.cga.sctp.transfers.TransferService;
import org.cga.sctp.transfers.reconciliation.TransferReconciliationRequest;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Collections.singletonMap;

@RestController
@RequestMapping("/transfers")
public class TransfersController {

    private final int PAGE_SIZE = 100;

    @Autowired
    private TransferService transferService;

    @GetMapping
    @Operation(description = "Retrieves Transfer List for the given Geolocation. Does not include full household detail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TransferListResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<?> getTransferListForLocation(@AuthenticatedUserDetails ApiUserDetails user,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "traditional-authority-code", required = false, defaultValue = "0") Long taCode,
                                                        @RequestParam(value = "village-cluster-code", required = false, defaultValue = "0") Long villageCluster,
                                                        @RequestParam(value = "zone-code", required = false, defaultValue = "0") Long zone,
                                                        @RequestParam(value = "village-code", required = false, defaultValue = "0") Long village) {

        Pageable pageable = Pageable.ofSize(PAGE_SIZE).withPage(page);
        long districtCode = user.getAccessTokenClaims().getDistrictCode().longValue();
        List<Transfer> transferListSummary = transferService.fetchTransferList(districtCode, taCode, villageCluster, zone, village, pageable);
        return ResponseEntity.ok(new TransferListResponse(page, PAGE_SIZE, transferListSummary.size(), transferListSummary));
    }

    @GetMapping("/pending")
    @Operation(description = "Retrieves Transfer List for the given Geolocation. Does not include full household detail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TransferListResponse.class)))
    })
    @IncludeGeneralResponses
    public ResponseEntity<?> getPendingTransferListForLocation(@AuthenticatedUserDetails ApiUserDetails user,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "traditional-authority-code", required = false, defaultValue = "0") Long taCode,
                                                        @RequestParam(value = "village-cluster-code", required = false, defaultValue = "0") Long villageCluster,
                                                        @RequestParam(value = "zone-code", required = false, defaultValue = "0") Long zone,
                                                        @RequestParam(value = "village-code", required = false, defaultValue = "0") Long village) {

        Pageable pageable = Pageable.ofSize(PAGE_SIZE).withPage(page);
        long districtCode = user.getAccessTokenClaims().getDistrictCode().longValue();
        List<Transfer> transferListSummary = transferService.fetchPendingTransferListByLocation(districtCode, taCode, villageCluster, zone, village, pageable);
        return ResponseEntity.ok(new TransferListResponse(page, PAGE_SIZE, transferListSummary.size(), transferListSummary));
    }

    /**
     * This endpoint is mostly intended for use by the App client
     * @param request detail fo transfers to update
     * @return
     */
    @PostMapping
    @RequestMapping({ "/update", "/perform" })
    @Operation(
            description = "Updates/performs Transfers with amounts disbursed, dates and personnel.  Applies only to manual transfers",
            tags = { "transfers", "manual-transfers" })
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @IncludeGeneralResponses
    public ResponseEntity<Object> postUpdateTransfers(@AuthenticatedUserDetails ApiUserDetails user,
                                                      @Validated @RequestBody TransferReconciliationRequest request) {
        // TODO: publish general event here about transfer being updated
        int noUpdated = transferService.performManualTransfers(request, user.getUserId());
        // TODO: better response structure...
        return ResponseEntity.ok(singletonMap("message", String.format("Updated %s Transfer records", noUpdated)));
    }

    @GetMapping
    @Operation(
            description = "Gets status of E-Payment/automated transfers",
            tags = { "transfers", "epayment-transfers" })
    @RequestMapping("/epayments")
    public ResponseEntity<Object> getEPaymentTransfersStatus(@RequestBody Object epaymentTransferRequestDto) {
        // TODO: Implement me!
        return ResponseEntity.badRequest().build();
    }
}
