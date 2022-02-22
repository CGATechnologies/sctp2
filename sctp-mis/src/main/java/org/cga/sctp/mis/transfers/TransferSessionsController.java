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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.targeting.CbtRanking;
import org.cga.sctp.targeting.EnrollmentService;
import org.cga.sctp.targeting.EnrollmentSessionView;
import org.cga.sctp.targeting.EnrolmentSessionRepository;
import org.cga.sctp.transfers.TransferEventHouseholdView;
import org.cga.sctp.transfers.TransferEventRepository;
import org.cga.sctp.transfers.TransferSession;
import org.cga.sctp.transfers.TransferSessionService;
import org.cga.sctp.transfers.parameters.EducationTransferParameter;
import org.cga.sctp.user.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/transfers/sessions")
public class TransferSessionsController extends BaseController  {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransferSessionService transferSessionService;

    @Autowired
    private TransferEventRepository transferEventRepository;

    @Autowired
    private EnrolmentSessionRepository enrolmentSessionRepository;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    public ModelAndView list(Pageable pageable) {
        var transferSessions = transferSessionService.findAllActive(pageable);
        return view("transfers/calculation/list")
                .addObject("transferSessions", transferSessions);
    }

    @PostMapping("/initiate")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public String initiateNewTransferFromEnrollment(@RequestParam("enrollment") Long enrollmentSessionId,
                                                    RedirectAttributes attributes) {
        // 1. Select only the households that are marked as enrolled from the enrollment session
        // 2. Create a new transfer_session (a session keeps track of transfer events), the transfer session may also track the periods
        // 3. For each enrolled household create a new transfer_session_event (which calculates the amounts for each household)

        EnrollmentSessionView sessionView = enrollmentService.getEnrollmentSession(enrollmentSessionId);
        if (sessionView == null) {
            return redirectWithDangerMessage(format("/targeting/enrolment?invalidSession=%s", enrollmentSessionId),
                "Enrollment session was not found or not active",
                attributes);
        }

        if (enrollmentService.sessionHasPendingHouseholdsToEnroll(enrollmentSessionId)) {
            return redirectWithDangerMessage(format("/targeting/enrolment?invalidSession=%s", enrollmentSessionId),
                    "Enrollment Session contains Households that have not been enrolled or marked ineligible",
                    attributes);
        }

        Long transferSessionId = -1L;
        TransferSession session = new TransferSession();
        session.setEnrollmentSessionId(enrollmentSessionId);
        session.setProgramId(-1L); // TODO: get program id somehow...
        session.setActive(true);
        session.setCreatedAt(LocalDateTime.now());
        session.setModifiedAt(session.getCreatedAt());

        transferSessionService.getTranferSessionRepository().save(session);

        transferSessionId = session.getId();

        Slice<CbtRanking> cbtRankingSlice = enrollmentService.getEnrolledHouseholds(sessionView, PageRequest.of(1,1000));
        List<Long> householdIds  = cbtRankingSlice.getContent().stream().map(CbtRanking::getHouseholdId).collect(Collectors.toList());

        transferSessionService.initiateTransfersForHouseholds(transferSessionId, householdIds, enrollmentSessionId);

        // See {@see #viewPerformCalculationPage}
        return redirectWithSuccessMessage(format("/transfers/sessions/%s/pre-calculation", transferSessionId),
            "New Transfer Session initiated successfully from enrolled households",
            attributes);
    }

    @GetMapping("/{session-id}/pre-calculation")
    public ModelAndView viewPerformCalculationPage(@PathVariable("session-id") Long sessionId,
                                                   RedirectAttributes attributes) {

        Optional<TransferSession> sessionOptional = transferSessionService.getTranferSessionRepository().findById(sessionId);
        if (sessionOptional.isEmpty()) {
            setDangerFlashMessage("Transfer Session does not exist or is not valid", attributes);
            return redirect("/transfers/sessions");
        }
        EnrollmentSessionView enrollmentSessionView = enrollmentService.getEnrollmentSession(sessionOptional.get().getEnrollmentSessionId());

        List<TransferEventHouseholdView> transferEvents = transferSessionService.findAllHouseholdsInSession(sessionId);

        TransferCalculationPageData pageData = new TransferCalculationPageData();
        pageData.setProgramInfo(null); // TODO: Get program id somewhere);
        pageData.setTransferSession(sessionOptional.get());
        pageData.setEnrollmentSession(enrollmentSessionView);
        pageData.setHouseholdRows(transferEvents);
        pageData.setHouseholdParams(transferSessionService.findAllActiveHouseholdParameters());

        Map<String, EducationTransferParameter> educationParamsMap = new HashMap<>();
        transferSessionService.findAllEducationTransferParameters().forEach(entry -> {
            educationParamsMap.put(entry.getEducationLevel().toString().toLowerCase(), entry);
        });

        pageData.setEducationParams(educationParamsMap);

        return view("/transfers/calculation/perform_precalculation")
                .addObject("pageData", pageData)
                .addObject("objectMapper", objectMapper); // for serializing data to JSON in the template
    }
}
