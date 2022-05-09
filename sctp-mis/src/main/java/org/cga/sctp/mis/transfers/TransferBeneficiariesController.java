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

package org.cga.sctp.mis.transfers;

import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.transfers.accounts.BeneficiaryAccountService;
import org.cga.sctp.transfers.TransferEventHouseholdView;
import org.cga.sctp.transfers.TransferService;
import org.cga.sctp.transfers.TransferSession;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/beneficiaries")
public class TransferBeneficiariesController extends SecuredBaseController {
    @Autowired
    private TransferService transferService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private BeneficiaryAccountService beneficiaryAccountService;


    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView viewListBeneficiaries(@RequestParam(value = "sessionId", required = false) Long sessionId,
                                              @RequestParam(value = "district", required = false) Long districtId) {
        Optional<TransferSession> transferSession = Optional.empty();
        List<TransferEventHouseholdView> householdList = new ArrayList<>();

        if (districtId != null) {
            transferSession = transferService.findLatestSessionInDistrict(districtId);
        } else if (sessionId != null) {
            transferSession = transferService.getTranferSessionRepository().findById(sessionId);
        }

        transferSession.ifPresent(session -> {
            householdList.addAll(transferService.findAllHouseholdsInSession(session.getId()));
        });

        return view("/transfers/beneficiaries/households")
                .addObject("districtId", districtId)
                .addObject("sessionId", sessionId)
                .addObject("districts", locationService.getActiveDistricts())
                .addObject("households", householdList);
    }

    @GetMapping("/export-accounts-template")
    @AdminAndStandardAccessOnly
    public ResponseEntity<?> downloadExportedAccountFormat(@RequestParam(value = "sessionId", required = false) Long sessionId,
                                                           @RequestParam(value = "district", required = false) Long districtId) {

        Optional<TransferSession> transferSession = Optional.empty();
        List<TransferEventHouseholdView> householdList = new ArrayList<>();

        if (districtId != null) {
            transferSession = transferService.findLatestSessionInDistrict(districtId);
        } else if (sessionId != null) {
            transferSession = transferService.getTranferSessionRepository().findById(sessionId);
        }

        transferSession.ifPresent(session -> {
            householdList.addAll(transferService.findAllHouseholdsInSession(session.getId()));
        });

        try {
            Path filePath = beneficiaryAccountService.exportBeneficiaryListToExcel(householdList);
            return ResponseEntity.status(200)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "filename=accounts.xlsx")
                    .body(Files.readAllBytes(filePath));
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to export beneficiaries", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
