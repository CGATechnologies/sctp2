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
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.transfers.parameters.EducationTransferParameter;
import org.cga.sctp.transfers.parameters.EducationTransferParameterRepository;
import org.cga.sctp.transfers.parameters.TransferParameter;
import org.cga.sctp.transfers.parameters.TransferParametersRepository;
import org.cga.sctp.user.AdminAccessOnly;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/transfers/parameters/education")
public class EducationTransferParameterController extends BaseController {

    @Autowired
    private EducationTransferParameterRepository educationTransferParameterRepository;

    @Autowired
    private TransferParametersRepository transferParametersRepository;

    @GetMapping
    public ModelAndView viewIndex() {
        List<EducationTransferParameter> educationParameterList = educationTransferParameterRepository.findAll();
        return view("/transfers/parameters/education/list")
                .addObject("educationParameters", educationParameterList);
    }

    @GetMapping("/new")
    @AdminAccessOnly
    public ModelAndView viewAdd() {
        List<TransferParameter> transferParameters = transferParametersRepository.findAllActive();
        return view("/transfers/parameters/education/new")
                .addObject("booleans", Booleans.VALUES)
                .addObject("educationLevels", EducationLevel.values())
                .addObject("transferParameters", transferParameters);
    }

    @PostMapping("/new")
    @AdminAccessOnly
    public ModelAndView processAdd(@AuthenticationPrincipal String username,
                                   @Validated @ModelAttribute EducationTransferParameterForm form,
                                   BindingResult result,
                                   RedirectAttributes attributes) {

        var existingParam = educationTransferParameterRepository.findDistinctByEducationLevel(form.getEducationLevel());
        if (existingParam != null) {
            LoggerFactory.getLogger(getClass()).info("Found the following parameter entry {}", existingParam);
            setWarningFlashMessage(format("Education parameter with that Education level ('%s') already exists.", form.getEducationLevel()), attributes);
            return redirect("/transfers/parameters/education");
        }

        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to Education parameter. Please fix the errors on the form", attributes);
            return view("/transfers/parameters/education/new")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("educationLevels", EducationLevel.values());
        }

        EducationTransferParameter educationParameter = new EducationTransferParameter();
        educationParameter.setTransferParameterId(form.getTransferParameterId());
        educationParameter.setEducationLevel(form.getEducationLevel());
        educationParameter.setActive(form.isActive().value);
        educationParameter.setAmount(form.getAmount());
        educationParameter.setCreatedAt(LocalDateTime.now());
        educationParameter.setModifiedAt(educationParameter.getCreatedAt());

        educationTransferParameterRepository.save(educationParameter);

        setSuccessFlashMessage("Education parameter saved successfully", attributes);
        return redirect("/transfers/parameters/education");
    }

    @GetMapping("/{parameter-id}/edit")
    @AdminAccessOnly
    public ModelAndView viewEdit(@AuthenticationPrincipal String username,
                                 @PathVariable("parameter-id") Long id,
                                 @ModelAttribute("form") EducationTransferParameterForm form,
                                 RedirectAttributes attributes) {

        Optional<EducationTransferParameter> parameterOptional = educationTransferParameterRepository.findById(id);
        if (parameterOptional.isEmpty()) {
            setDangerFlashMessage("Failed to find Education parameter", attributes);
            return redirect("/transfers/parameters/education");
        }

        EducationTransferParameter educationParameter = parameterOptional.get();

        form.setId(id);
        form.setActive(Booleans.of(educationParameter.isActive()));
        form.setAmount(educationParameter.getAmount());
        form.setEducationLevel(educationParameter.getEducationLevel());

        return view("/transfers/parameters/education/edit")
                .addObject("booleans", Booleans.VALUES)
                .addObject("educationLevels", EducationLevel.values());
    }

    @PostMapping("/{parameter-id}/edit")
    @AdminAccessOnly
    public ModelAndView processEdit(@AuthenticationPrincipal String username,
                                    @PathVariable("parameter-id") Long id,
                                    @Validated @ModelAttribute EducationTransferParameterForm form,
                                    BindingResult result,
                                    RedirectAttributes attributes) {

        Optional<EducationTransferParameter> parameterOptional = educationTransferParameterRepository.findById(id);
        if (parameterOptional.isEmpty()) {
            setDangerFlashMessage("Failed to find Education parameter", attributes);
            return redirect("/transfers/parameters/education");
        }

        EducationTransferParameter educationParameter = parameterOptional.get();
        if (result.hasErrors()) {
            setWarningFlashMessage("Failed to Education parameter. Please fix the errors on the form", attributes);
            return view("/transfers/parameters/education/edit")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("educationLevels", EducationLevel.values());
        }

        // TODO: check if there is a parameter with a condition that's conflicting with the one coming in
        educationParameter.setEducationLevel(form.getEducationLevel());
        educationParameter.setAmount(form.getAmount());
        educationParameter.setActive(form.isActive().value);
        educationParameter.setModifiedAt(LocalDateTime.now());

        educationTransferParameterRepository.save(educationParameter);

        setSuccessFlashMessage("Education parameter updated successfully", attributes);
        return redirect("/transfers/parameters/education");
    }
}
