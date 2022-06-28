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
import org.cga.sctp.transfers.parameters.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/transfers/parameters/households")
public class HouseholdParametersController extends BaseController {

    @Autowired
    private HouseholdTransferParametersRepository householdTransferParametersRepository;

    @Autowired
    private TransferParametersRepository transferParametersRepository;

    @GetMapping
    public ModelAndView viewIndex() {
        List<HouseholdTransferParameter> householdParameterList = householdTransferParametersRepository.findAll();
        return view("/transfers/parameters/households/list")
                .addObject("householdParameters", householdParameterList);
    }

    @GetMapping("/new")
    public ModelAndView viewAdd() {
        List<TransferParameter> transferParameters = transferParametersRepository.findAllActive();
        return view("/transfers/parameters/households/new")
                .addObject("booleans", Booleans.VALUES)
                .addObject("conditions", HouseholdParameterCondition.values())
                .addObject("transferParameters", transferParameters);
    }

    @PostMapping("/new")
    public ModelAndView processAdd(@AuthenticationPrincipal String username,
                                   @Validated @ModelAttribute HouseholdTransferParameterForm form,
                                   BindingResult result,
                                   RedirectAttributes attributes) {
        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to Household parameter. Please fix the errors on the form", attributes);
            return view("/transfers/parameters/households/new")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("conditions", HouseholdParameterCondition.values());
        }

        HouseholdTransferParameter householdParameter = new HouseholdTransferParameter();

        // TODO: check if there is a parameter with a condition that's conflicting with the one coming in
        householdParameter.setTransferParameterId(form.getTransferParameterId());
        householdParameter.setNumberOfMembers(form.getNumberOfMembers());
        householdParameter.setActive(form.isActive().value);
        householdParameter.setAmount(form.getAmount());
        householdParameter.setCondition(form.getCondition());
        householdParameter.setCreatedAt(LocalDateTime.now());
        householdParameter.setModifiedAt(householdParameter.getCreatedAt());

        householdTransferParametersRepository.save(householdParameter);

        setSuccessFlashMessage("Household parameter saved successfully", attributes);
        return redirect("/transfers/parameters/households");
    }

    @GetMapping("/{parameter-id}/edit")
    public ModelAndView viewEdit(@AuthenticationPrincipal String username,
                                 @PathVariable("parameter-id") Long id,
                                 @ModelAttribute("form") HouseholdTransferParameterForm form,
                                 RedirectAttributes attributes) {

        Optional<HouseholdTransferParameter> parameterOptional = householdTransferParametersRepository.findById(id);
        if (parameterOptional.isEmpty()) {
            setDangerFlashMessage("Failed to find Household parameter", attributes);
            return redirect("/transfers/parameters/households");
        }

        HouseholdTransferParameter householdParameter = parameterOptional.get();

        form.setId(id);
        form.setActive(Booleans.of(householdParameter.isActive()));
        form.setAmount(householdParameter.getAmount());
        form.setNumberOfMembers(householdParameter.getNumberOfMembers());
        form.setCondition(householdParameter.getCondition());

        return view("/transfers/parameters/households/edit")
                .addObject("booleans", Booleans.VALUES)
                .addObject("conditions", HouseholdParameterCondition.values());
    }

    @PostMapping("/{parameter-id}/edit")
    public ModelAndView processEdit(@AuthenticationPrincipal String username,
                                    @PathVariable("parameter-id") Long id,
                                    @Validated @ModelAttribute HouseholdTransferParameterForm form,
                                    BindingResult result,
                                    RedirectAttributes attributes) {

        Optional<HouseholdTransferParameter> parameterOptional = householdTransferParametersRepository.findById(id);
        if (parameterOptional.isEmpty()) {
            setDangerFlashMessage("Failed to find Household parameter", attributes);
            return redirect("/transfers/parameters/households");
        }

        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to Household parameter. Please fix the errors on the form", attributes);
            return view("/transfers/parameters/households/edit")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("conditions", HouseholdParameterCondition.values());
        }

        HouseholdTransferParameter householdParameter = parameterOptional.get();

        householdParameter.setCondition(form.getCondition());
        householdParameter.setNumberOfMembers(form.getNumberOfMembers());
        householdParameter.setAmount(form.getAmount());
        householdParameter.setActive(form.isActive().value);
        householdParameter.setModifiedAt(LocalDateTime.now());

        householdTransferParametersRepository.save(householdParameter);

        setSuccessFlashMessage("Household parameter updated successfully", attributes);
        return redirect("/transfers/parameters/households");
    }
}
