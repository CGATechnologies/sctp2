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

package org.cga.sctp.mis.transfers.topups;

import org.cga.sctp.funders.Funder;
import org.cga.sctp.funders.FundersService;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.transfers.topups.NewTopUpParams;
import org.cga.sctp.transfers.topups.TopUp;
import org.cga.sctp.transfers.topups.TopUpService;
import org.cga.sctp.transfers.topups.TopUpType;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/topups")
public class TransferTopUpsController extends SecuredBaseController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private FundersService fundersService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TopUpService topUpService;

    @GetMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView getNewPage() {
        List<Program> programs = programService.getActivePrograms();
        List<Location> districts =  locationService.getActiveDistricts();
        List<Funder> funders = fundersService.getActiveFunders();
        return view("transfers/topups/new")
            .addObject("booleans", Booleans.values())
            .addObject("topupTypes", TopUpType.values())
            .addObject("districts", districts)
            .addObject("programs", programs)
            .addObject("funders", funders);
    }

    @PostMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView postNewPage(@AuthenticatedUserDetails AuthenticatedUser user,
                                    @ModelAttribute @Validated NewTopUpParams form,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        List<Program> programs = programService.getActivePrograms();
        List<Location> districts =  locationService.getActiveDistricts();
        List<Funder> funders = fundersService.getActiveFunders();

        if (result.hasErrors()) {
            return withDangerMessage("transfers/topups/new", "Please correct errors on the form")
                .addObject("booleans", Booleans.values())
                .addObject("topupTypes", TopUpType.values())
                .addObject("districts", districts)
                .addObject("programs", programs)
                .addObject("funders", funders);
        }

        Optional<TopUp> topUp = topUpService.newTopup(form);
        if (topUp.isPresent()) {
            return view(redirectWithSuccessMessage("/transfers/topups", "TopUp created successfully", redirectAttributes));
        }

        return withDangerMessage( "transfers/topups/new", "Failed to create topups due to unknown error")
                .addObject("booleans", Booleans.values())
                .addObject("topupTypes", TopUpType.values())
                .addObject("districts", districts)
                .addObject("programs", programs)
                .addObject("funders", funders);
    }

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView getIndex() {
        List<TopUp> topups = topUpService.findAllActive();
        return view("transfers/topups/list")
            .addObject("topups", topups);
    }

}
