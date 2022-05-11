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
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.targeting.EnrollmentService;
import org.cga.sctp.transfers.TransferEventHouseholdView;
import org.cga.sctp.transfers.TransferService;
import org.cga.sctp.transfers.TransferSession;
import org.cga.sctp.transfers.parameters.EducationTransferParameter;
import org.cga.sctp.transfers.parameters.TransferParametersService;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.cga.sctp.user.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/sessions")
public class TransferSessionsController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransferParametersService transferParametersService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView index(Pageable pageable) {
        var transferSessions = transferService.findAllActiveSessions(pageable);
        // TODO: Remember transfer Summaries and transfer sessions are two different concepts
        return view("transfers/index")
                .addObject("transferSessions", transferSessions)
                .addObject("transferSummaries", new Object()); // FIXME: fetch summary
    }



    @GetMapping("/initiate/step1")
    @AdminAndStandardAccessOnly
    public ModelAndView getInitiateStep1() {
        List<Program> programs = programService.getActivePrograms();
        List<Location> districts = locationService.getActiveDistricts();

        return view("/transfers/initiate/step1")
                .addObject("programs", programs)
                .addObject("districts", districts);
    }

    @GetMapping("/initiate/step2")
    @AdminAndStandardAccessOnly
    public ModelAndView viewCalculationStep2(@RequestParam("programId") Long programId,
                                             @RequestParam("districtId") Long districtId) {
        Program program = programService.getProgramById(programId);
        Location district = locationService.findById(districtId);

        return view("/transfers/initiate/step2")
                .addObject("program", program)
                .addObject("district", district)
                .addObject("householdParameters", transferParametersService.findAllActiveHouseholdParameters())
                .addObject("educationBonuses", transferParametersService.findAllEducationTransferParameters())
                .addObject("educationIncentives", transferParametersService.findAllEducationTransferParameters());
    }

    @PostMapping("/initiate/step3")
    @AdminAndStandardAccessOnly
    public ModelAndView postInitiateStep2(@AuthenticatedUserDetails AuthenticatedUser user,
                                          @Validated @ModelAttribute InitiateTransferForm form,
                                          BindingResult result,
                                          RedirectAttributes attributes) {
        if (result.hasErrors()) {
            LoggerFactory.getLogger(getClass()).error("Failed to initiate transfers: {}", result.getAllErrors());
            return withDangerMessage("/transfers/initiate/step1", "Failed to create Transfer records. Please fix the errors on the form")
                    .addObject("programs", programService.getActivePrograms())
                    .addObject("districts", locationService.getActiveDistricts())
                    .addObject("householdParameters", transferParametersService.findAllActiveHouseholdParameters())
                    .addObject("educationBonuses", transferParametersService.findAllEducationTransferParameters())
                    .addObject("educationIncentives", transferParametersService.findAllEducationTransferParameters());

        }

        boolean householdsPendingEnrollment = false;
        if (form.getEnrollmentSessionId() != null && form.getEnrollmentSessionId() > 0L) {
            householdsPendingEnrollment = enrollmentService.sessionHasHouseholdsWithPreEligibleOrNotYetEnrolled(form.getEnrollmentSessionId());
        }

        if (householdsPendingEnrollment) {
            LoggerFactory.getLogger(getClass()).error("Failed to update agency: {}", attributes);
            return withDangerMessage("/transfers/initiate/step1", "Some households have not yet been enrolled or marked Ineligible.")
                    .addObject("programs", programService.getActivePrograms())
                    .addObject("districts", locationService.getActiveDistricts())
                    .addObject("householdParameters", transferParametersService.findAllActiveHouseholdParameters())
                    .addObject("educationBonuses", transferParametersService.findAllEducationTransferParameters())
                    .addObject("educationIncentives", transferParametersService.findAllEducationTransferParameters());
        }

        TransferSession transferSession = new TransferSession();
        transferSession.setProgramId(form.getProgramId());
        transferSession.setActive(true);
        transferSession.setDistrictId(form.getDistrictId());
        transferSession.setCreatedAt(LocalDateTime.now());
        transferSession.setModifiedAt(transferSession.getCreatedAt());

        Program program = programService.getProgramById(form.getProgramId());
        Location location = locationService.findById(form.getDistrictId());

        transferService.initiateTransfers(location, transferSession, user.id());

        return redirect(format("/transfers/sessions/%s/pre-calculation", transferSession.getId()));
    }

    @GetMapping("/{session-id}/pre-calculation")
    @AdminAndStandardAccessOnly
    public ModelAndView viewPerformCalculationPage(@PathVariable("session-id") Long sessionId,
                                                   RedirectAttributes attributes) {

        Optional<TransferSession> sessionOptional = transferService.getTranferSessionRepository().findById(sessionId);
        if (sessionOptional.isEmpty()) {
            setDangerFlashMessage("Transfer Session does not exist or is not valid", attributes);
            return redirect("/transfers/sessions");
        }
        List<TransferEventHouseholdView> transferEvents = transferService.findAllHouseholdsInSession(sessionId);

        TransferCalculationPageData pageData = new TransferCalculationPageData();
        pageData.setProgramInfo(null); // TODO: Get program id somewhere);
        pageData.setTransferSession(sessionOptional.get());
        pageData.setHouseholdRows(transferEvents);
        pageData.setHouseholdParams(transferParametersService.findAllActiveHouseholdParameters());

        Map<String, EducationTransferParameter> educationParamsMap = new HashMap<>();
        transferParametersService.findAllEducationTransferParameters().forEach(entry -> {
            educationParamsMap.put(entry.getEducationLevel().toString().toLowerCase(), entry);
        });

        pageData.setEducationParams(educationParamsMap);

        return view("/transfers/calculation/pre-calculation")
                .addObject("pageData", pageData)
                .addObject("objectMapper", objectMapper); // for serializing data to JSON in the template
    }


}
