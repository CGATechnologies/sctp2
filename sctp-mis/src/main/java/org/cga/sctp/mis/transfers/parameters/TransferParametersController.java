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

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.transfers.parameters.EducationTransferParameterRepository;
import org.cga.sctp.transfers.parameters.HouseholdTransferParametersRepository;
import org.cga.sctp.transfers.parameters.TransferParameter;
import org.cga.sctp.transfers.parameters.TransferParametersRepository;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/parameters")
public class TransferParametersController extends BaseController {

    @Autowired
    private TransferParametersRepository transferParameterRepository;

    @Autowired
    private EducationTransferParameterRepository educationTransferParameterRepository;

    @Autowired
    private HouseholdTransferParametersRepository householdTransferParametersRepository;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView getParametersList() {
        List<TransferParameter> transferParameters = transferParameterRepository.findAll();
        return view("transfers/parameters/index")
                .addObject("transferParameters", transferParameters);
    }

    @GetMapping("/view/{parameter-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getParametersList(@PathVariable("parameter-id") Long parameterId) {
        Optional<TransferParameter> transferParameterOptional = transferParameterRepository.findById(parameterId);
        if (transferParameterOptional.isEmpty()) {
            return redirect("/transfers/parameters");
        }

        return view("transfers/parameters/view")
                .addObject("transferParameter", transferParameterOptional.get())
                .addObject("householdParameters", householdTransferParametersRepository.findByTransferParameterId(parameterId))
                .addObject("educationBonuses", educationTransferParameterRepository.findByTransferParameterId(parameterId));
    }

    @GetMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView viewNew() {
        return view("transfers/parameters/new")
                .addObject("booleans", Booleans.VALUES);
    }

    @PostMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView processAdd(@AuthenticatedUserDetails AuthenticatedUser user,
                                   @Validated @ModelAttribute TransferParameterForm form,
                                   BindingResult result,
                                   RedirectAttributes attributes) {
        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to Save Parameter. Please fix the errors on the form", attributes);
            return view("/transfers/parameters/new")
                    .addObject("booleans", Booleans.VALUES);
        }

        TransferParameter transferParameter = new TransferParameter();
        transferParameter.setTitle(form.getTitle());
        transferParameter.setActive(form.getActive().value);
        transferParameter.setCreatedBy(user.id());
        transferParameter.setCreatedAt(LocalDateTime.now());
        transferParameter.setUpdatedAt(LocalDateTime.now());

        if (transferParameterRepository.save(transferParameter) != null) {
            return redirect(format("/transfers/parameters/add/%s/household-parameters", transferParameter.getId()));
        }

        return view("transfers/parameters/new")
                .addObject("booleans", Booleans.VALUES);
    }
}
