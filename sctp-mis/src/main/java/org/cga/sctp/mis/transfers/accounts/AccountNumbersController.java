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

package org.cga.sctp.mis.transfers.accounts;

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.mis.file_upload.FileUploadService;
import org.cga.sctp.transfers.TransferSession;
import org.cga.sctp.transfers.accounts.BeneficiaryAccountService;
import org.cga.sctp.transfers.accounts.TransferAccountNumberList;
import org.cga.sctp.transfers.agencies.TransferAgency;
import org.cga.sctp.transfers.agencies.TransferAgencyService;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/assign-accounts")
public class AccountNumbersController extends SecuredBaseController {
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private TusFileUploadService tusFileUploadService;

    @Autowired
    private BeneficiaryAccountService beneficiaryAccountService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TransferAgencyService transferAgencyService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView getAssignAccounts(@RequestParam("district-id") Long districtId) {
        Location district = locationService.findById(districtId);
        List<TransferAgency> transferAgencyList = transferAgencyService.fetchAllTransferAgencies();
        return view("/transfers/beneficiaries/assign-accounts")
                .addObject("program", new Object())
                .addObject("accountsSummary", new Object())
                .addObject("district", district)
                .addObject("transferAgencies", transferAgencyList);
    }

    @PostMapping
    @RequestMapping(
            value = {"/{session-id}/upload", "/{session-id}/upload/**"},
            method = {RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.HEAD, RequestMethod.DELETE,
                    RequestMethod.OPTIONS, RequestMethod.GET}
    )
    public void processUpload(@PathVariable("session-id") Long sessionId,
                              @AuthenticatedUserDetails AuthenticatedUser user,
                              HttpServletRequest servletRequest,
                              HttpServletResponse servletResponse) throws IOException {

        tusFileUploadService.process(servletRequest, servletResponse, user.username());

        try {
            UploadInfo uploadInfo = tusFileUploadService.getUploadInfo(servletRequest.getRequestURI(), user.username());
            if (uploadInfo != null) {
                if (!uploadInfo.isUploadInProgress()) {
                    try (InputStream is = tusFileUploadService.getUploadedBytes(servletRequest.getRequestURI(), user.username())) {
                        Optional<Path> uploadedFilePath = fileUploadService.moveToStagingDirectoryAndGetPath(is, uploadInfo);
                        if (uploadedFilePath.isEmpty()) {
                            TransferSession transferSession = null;
                            TransferPeriod transferPeriod = null;
                            TransferAccountNumberList accountNumberList = beneficiaryAccountService.extractAccountNumberFromCSV(uploadedFilePath.get());
                            beneficiaryAccountService.assignAccountNumbers(transferSession, transferPeriod, accountNumberList);
                        }
                    }
                    // upload is finished, move to staging directory, Update to processing
                    // delete from temp
                    tusFileUploadService.deleteUpload(servletRequest.getRequestURI(), user.username());
                }
            }
        } catch (TusException | IOException e) {
            LOG.error("Error getting upload information", e);
            servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
