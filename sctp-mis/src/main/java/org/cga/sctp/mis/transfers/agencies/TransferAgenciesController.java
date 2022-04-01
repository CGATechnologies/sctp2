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

package org.cga.sctp.mis.transfers.agencies;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationInfo;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.transfers.agencies.TransferAgency;
import org.cga.sctp.transfers.agencies.TransferAgencyAssignment;
import org.cga.sctp.transfers.agencies.TransferAgencyService;
import org.cga.sctp.transfers.agencies.TransferMethod;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transfers/agencies")
public class TransferAgenciesController extends BaseController {

    @Autowired
    private TransferAgencyService transferAgencyService;

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ModelAndView listAgencies() {
        return view("transfers/agencies/list")
                .addObject("transferAgencies", transferAgencyService.fetchAllTransferAgencies());
    }

    @GetMapping("/new")
    @AdminAccessOnly
    public ModelAndView createPage() {
        List<Location> locations = locationService.getActiveDistricts();
        return view("/transfers/agencies/new")
                .addObject("options", Booleans.VALUES)
                .addObject("locations", locations);
    }

    @PostMapping("/new")
    @AdminAccessOnly
    public ModelAndView processCreatePage(@AuthenticationPrincipal String username,
                                  @Validated @ModelAttribute TransferAgencyForm form,
                                  BindingResult result,
                                  RedirectAttributes attributes) {

        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to save Agency please fix the errors on the form", attributes);
            LoggerFactory.getLogger(getClass()).error("Failed to update agency: {}", attributes);
            return view("transfers/agencies/new")
                    .addObject("options", Booleans.VALUES)
                    .addObject("form", form);
        }

        TransferAgency transferAgency = new TransferAgency();

        transferAgency.setName(form.getName());
        transferAgency.setActive(form.isActive().value);
        transferAgency.setAddress(form.getAddress());
        transferAgency.setRepresentativeName(form.getRepresentativeName());
        transferAgency.setRepresentativeEmail(form.getRepresentativeEmail());
        transferAgency.setWebsite(form.getWebsite());
        transferAgency.setBranch(form.getBranch());
        transferAgency.setCreatedAt(LocalDate.now());
        transferAgency.setModifiedAt(transferAgency.getCreatedAt());

        publishGeneralEvent("%s created agency: name=%s", username, transferAgency.getName());

        transferAgencyService.getTransferAgenciesRepository().save(transferAgency);

        return redirect("/transfers/agencies");
    }

    @GetMapping("/{transfer-agency-id}/edit")
    @AdminAccessOnly
    public ModelAndView viewEditPage(@PathVariable("transfer-agency-id") Long id,
                                     @ModelAttribute("form") TransferAgencyForm form,
                                     BindingResult result,
                                     RedirectAttributes attributes) {
        TransferAgency transferAgency = transferAgencyService.getTransferAgenciesRepository().getOne(id);
        if (transferAgency == null) {
            setDangerFlashMessage("Transfer Agency does not exist", attributes);
            return redirect("/transfers/agencies");
        }

        form.setId(transferAgency.getId());
        form.setName(transferAgency.getName());
        form.setActive(Booleans.of(transferAgency.isActive()));
        form.setLocationId(transferAgency.getLocationId());
        form.setWebsite(transferAgency.getWebsite());
        form.setBranch(transferAgency.getBranch());
        form.setPhone(transferAgency.getPhone());
        form.setAddress(transferAgency.getAddress());
        form.setRepresentativeName(transferAgency.getRepresentativeName());
        form.setRepresentativeEmail(transferAgency.getRepresentativeEmail());
        form.setRepresentativePhone(transferAgency.getRepresentativePhone());

        List<Location> locations = locationService.getActiveDistricts();
        return view("/transfers/agencies/edit")
                .addObject("options", Booleans.VALUES)
                .addObject("locations", locations);
    }

    @PostMapping("/{transfer-agency-id}/edit")
    @AdminAccessOnly
    public ModelAndView processEditPage(@AuthenticationPrincipal String username,
                                        @PathVariable("transfer-agency-id") Long id,
                                        @Validated @ModelAttribute TransferAgencyForm form,
                                        BindingResult result,
                                        RedirectAttributes attributes) {

        TransferAgency transferAgency = transferAgencyService.getTransferAgenciesRepository().getOne(id);
        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to Update Agency please fix the errors on the form", attributes);
            LoggerFactory.getLogger(getClass()).error("Failed to update agency: {}", attributes);
            return view("transfers/agencies/new")
                    .addObject("options", Booleans.VALUES)
                    .addObject("form", form);
        }

        transferAgency.setName(form.getName());
        transferAgency.setActive(form.isActive().value);
        transferAgency.setAddress(form.getAddress());
        transferAgency.setPhone(form.getPhone());
        transferAgency.setRepresentativeName(form.getRepresentativeName());
        transferAgency.setRepresentativeEmail(form.getRepresentativeEmail());
        transferAgency.setRepresentativePhone(form.getRepresentativePhone());
        transferAgency.setWebsite(form.getWebsite());
        transferAgency.setBranch(form.getBranch());
        transferAgency.setLocationId(form.getLocationId());

        transferAgency.setModifiedAt(LocalDate.now());

        publishGeneralEvent("%s updated agency: name=%s", username, transferAgency.getName());

        transferAgencyService.getTransferAgenciesRepository().save(transferAgency);

        return redirect(String.format("/transfers/agencies/%s/view",id));
    }

    @GetMapping("/{transfer-agency-id}/view")
    public ModelAndView viewPage(@PathVariable("transfer-agency-id") Long id) {
        TransferAgency transferAgency = transferAgencyService.getTransferAgenciesRepository().getOne(id);
        return view("/transfers/agencies/view")
                .addObject("transferAgency", transferAgency);
    }

    @GetMapping("/assign")
    @AdminAccessOnly
    public ModelAndView viewAssignPage() {
        List<TransferAgency> transferAgencies = transferAgencyService.fetchAllTransferAgencies();
        List<Location> districts = locationService.getActiveDistricts();
        List<LocationInfo> tas = locationService.getByType(LocationType.SUBNATIONAL2);

        return view("/transfers/agencies/assign")
                .addObject("transferAgencies", transferAgencies)
                .addObject("transferMethodOptions", TransferMethod.values())
                .addObject("options", Booleans.VALUES)
                .addObject("districts", districts)
                .addObject("traditionalAuthorities", tas);
    }
}
