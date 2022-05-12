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

package org.cga.sctp.mis.transfers.periods;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.transfers.agencies.TransferAgency;
import org.cga.sctp.transfers.agencies.TransferAgencyService;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.cga.sctp.transfers.periods.TransferPeriodService;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/periods")
public class TransferPeriodController extends BaseController {
    @Autowired
    private LocationService locationService;

    @Autowired
    private TransferAgencyService transferAgencyService;

    @Autowired
    private TransferPeriodService transferPeriodService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView index() {
        List<TransferPeriod> transferPeriodList = transferPeriodService.findAll();
        return view("/transfers/periods/index")
                .addObject("transferPeriods", transferPeriodList);
    }

    @GetMapping("/open-new")
    @AdminAndStandardAccessOnly
    public ModelAndView viewCreateTransferPeriod(@RequestParam("transfer-agency-id") Long transferAgencyId,
                                                 @RequestParam("location-id") Long locationId) {

        // TODO: flash message
        if (transferAgencyId == null || locationId == null) {
            return redirect("/transfers/periods");
        }

        Optional<TransferAgency> transferAgency = transferAgencyService.findById(transferAgencyId);
        if (transferAgency.isEmpty()) {
            return redirect("/transfers/periods");
        }

        Location location = locationService.findById(locationId);

        return view("/transfers/periods/new")
                .addObject("transferAgency", transferAgency.get())
                .addObject("location", location)
                .addObject("lastPeriod", null);
    }

    @PostMapping("/open-new")
    @AdminAndStandardAccessOnly
    public ModelAndView handleCreateTransferPeriod(@AuthenticatedUserDetails AuthenticatedUser user,
                                                   @RequestParam("agency") Long transferAgencyId,
                                                   @Validated @ModelAttribute TransferPeriodForm form,
                                                   RedirectAttributes attributes) {


        TransferPeriod newPeriod = new TransferPeriod();

        newPeriod.setTransferSessionId(form.getTransferSessionId());
        newPeriod.setStartDate(form.getStartDate());
        newPeriod.setEndDate(form.getEndDate());
        newPeriod.setName(form.getName());
        newPeriod.setProgramId(form.getProgramId());

        transferPeriodService.openNewPeriod(newPeriod);


        return redirect(String.format("/transfers/periods/%d", newPeriod.getId()));
    }

    @GetMapping("/close")
    @AdminAndStandardAccessOnly
    public ModelAndView viewCloseTransferPeriod() {
        return view("/transfers/periods/close");
    }

    @GetMapping("/in-district/{district-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView viewGetTransferPeriodsInDistrict(@PathVariable("district-id") Long districtId) {
        Location district = locationService.findById(districtId);
        List<TransferPeriod> transferPeriods = transferPeriodService.findAllByDistrictId(districtId);

        return view("transfers/periods/list_by_district")
                .addObject("district", district)
                .addObject("transferPeriods", transferPeriods);
    }
}
