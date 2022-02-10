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

package org.cga.sctp.mis.transfers.parameters;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.transfers.parameters.LocationTransferParameter;
import org.cga.sctp.transfers.parameters.LocationTransferParametersRepository;
import org.cga.sctp.user.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/transfers/parameters/locations")
public class LocationTransferParameterController extends BaseController {

    @Autowired
    private LocationTransferParametersRepository locationTransferParametersRepository;

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ModelAndView viewIndex() {
        List<LocationTransferParameter> transferParameters = locationTransferParametersRepository.findAll();
        return view("transfers/parameters/locations/list")
                .addObject("transferParameters", transferParameters);
    }

    @GetMapping("/new")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView viewNew() {
        List<Location> locations = locationService.getActiveDistricts();
        return view("transfers/parameters/locations/new")
                .addObject("options", Booleans.VALUES)
                .addObject("locations", locations);
    }

    @PostMapping("/new")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView processNew(@AuthenticationPrincipal String username,
                                   @Validated @ModelAttribute LocationTransferParameterForm form,
                                   BindingResult result,
                                   RedirectAttributes attributes) {

        List<Location> locations = locationService.getActiveDistricts();

        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to save parameters please fix the errors on the form", attributes);
            return view("transfers/parameters/locations/new")
                    .addObject("options", Booleans.VALUES)
                    .addObject("locations", locations);
        }

        LocationTransferParameter locationTransferParameter = new LocationTransferParameter();
        locationTransferParameter.setActive(form.getActive());
        locationTransferParameter.setLocationId(form.getLocationId());
        locationTransferParameter.setAmount(form.getAmount());
        locationTransferParameter.setCreatedAt(LocalDateTime.now());
        locationTransferParameter.setModifiedAt(locationTransferParameter.getCreatedAt());

        locationTransferParametersRepository.save(locationTransferParameter);

        return view("transfers/parameters/locations/new")
                .addObject("options", Booleans.VALUES)
                .addObject("locations", locations);
    }

    @GetMapping("/{location-paramaeter-id}/edit")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView viewEdit() {
        List<Location> locations = locationService.getActiveDistricts();
        return view("transfers/parameters/locations/edit")
                .addObject("options", Booleans.VALUES)
                .addObject("locations", locations);
    }
}