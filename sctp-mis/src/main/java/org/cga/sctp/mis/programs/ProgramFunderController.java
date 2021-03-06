/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
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

package org.cga.sctp.mis.programs;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.program.ProgrammeType;
import org.cga.sctp.user.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/programs/{program-id}/funders")
public class ProgramFunderController extends BaseController {

    @Autowired
    private ProgramService programService;

    private Program findProgramById(Long id, RedirectAttributes attributes) {
        Program program = programService.getProgramById(id);
        if (program == null) {
            setDangerFlashMessage("Selected program does not exist.", attributes);
        }
        return program;
    }

    private ModelAndView redirectToPrograms() {
        return redirect("/programs");
    }

    private ModelAndView redirectToProgramFundersEditor(Long id) {
        return redirect(format("/programs/%d/funders/edit", id));
    }

    private ModelAndView redirectToProjectFundersEditor(Long id) {
        return redirect(format("/programs/%d/projects/funders/edit", id));
    }

    @GetMapping
    @Secured({RoleConstants.ROLE_STANDARD, RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView index(@PathVariable("program-id") Long programId, RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        return view("/programs/funders/list")
                .addObject("program", program)
                .addObject("funders", programService.getProgramFunders(programId));
    }

    @GetMapping("/edit")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView edit(@PathVariable("program-id") Long programId, RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        return view("/programs/funders/edit")
                .addObject("program", program)
                .addObject("available", programService.getAvailableProgramFunders(programId))
                .addObject("funders", programService.getProgramFunders(programId));
    }

    @PostMapping("/remove")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView removeFunders(
            @AuthenticationPrincipal String username,
            @PathVariable("program-id") Long programId,
            @Validated @ModelAttribute ProgramFundersForm programFundersForm,
            BindingResult bindingResult,
            RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        if (bindingResult.hasErrors()) {
            return withDangerMessage("/programs/funders/edit",
                    "There was an error processing that request. Please try again")
                    .addObject("program", program)
                    .addObject("available", programService.getAvailableProgramFunders(programId))
                    .addObject("funders", programService.getProgramFunders(programId));
        }
        programService.removeProgramFunders(program, programFundersForm.getFunderIds());

        publishGeneralEvent("%s removed %,d funders from %s %s.",
                username,
                programFundersForm.getFunderIds().size(),
                program.getName(),
                program.getProgrammeType().title.toLowerCase()
        );

        setSuccessFlashMessage(format("Successfully removed funders from %s.",
                program.getProgrammeType().title.toLowerCase()), attributes);

        return (program.getProgrammeType() == ProgrammeType.PROGRAMME) ?
                redirectToProgramFundersEditor(programId) : redirectToProjectFundersEditor(programId);
    }

    @PostMapping("/add")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    public ModelAndView addFunders(
            @AuthenticationPrincipal String username,
            @PathVariable("program-id") Long programId,
            @Validated @ModelAttribute ProgramFundersForm programFundersForm,
            BindingResult bindingResult,
            RedirectAttributes attributes) {
        Program program = findProgramById(programId, attributes);
        if (program == null) {
            return redirectToPrograms();
        }
        if (bindingResult.hasErrors()) {
            return withDangerMessage("/programs/funders/edit",
                    "There was an error processing that request. Please try again")
                    .addObject("program", program)
                    .addObject("available", programService.getAvailableProgramFunders(programId))
                    .addObject("funders", programService.getProgramFunders(programId));
        }

        programService.addProgramFunders(program, programFundersForm.getFunderIds());

        publishGeneralEvent("%s added %,d funders to %s %s.",
                username,
                programFundersForm.getFunderIds().size(),
                program.getName(),
                program.getProgrammeType().title.toLowerCase()
        );

        setSuccessFlashMessage(
                format("Successfully added funders to %s.", program.getProgrammeType().title.toLowerCase()),
                attributes
        );

        return (program.getProgrammeType() == ProgrammeType.PROGRAMME) ?
                redirectToProgramFundersEditor(programId) : redirectToProjectFundersEditor(programId);
    }
}
