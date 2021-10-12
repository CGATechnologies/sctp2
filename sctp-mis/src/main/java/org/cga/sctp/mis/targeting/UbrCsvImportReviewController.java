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

package org.cga.sctp.mis.targeting;

import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.targeting.exchange.DataImportObject;
import org.cga.sctp.targeting.exchange.DataImportService;
import org.cga.sctp.targeting.exchange.DataImportView;
import org.cga.sctp.targeting.importation.ImportTaskService;
import org.cga.sctp.targeting.importation.UbrHouseholdImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/data-import/from-ubr-csv/{import-id}/review")
public class UbrCsvImportReviewController extends SecuredBaseController {

    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private ImportTaskService taskService;

    private ModelAndView redirectToReview(long id) {
        return redirect(format("/data-import/from-ubr-csv/%d/review", id));
    }

    private DataImportView getImport(Long id, RedirectAttributes attributes) {
        DataImportView dataImport = dataImportService.findImportViewByIdAndStatus(id,
                DataImportObject.ImportSource.UBR_CSV, DataImportObject.ImportStatus.Review);
        if (dataImport == null) {
            setDangerFlashMessage("Cannot find this session import.", attributes);
        }
        return dataImport;
    }

    @GetMapping
    ModelAndView index(@PathVariable("import-id") Long id, RedirectAttributes attributes, Pageable pageable) {
        DataImportView dataImport = getImport(id, attributes);
        if (dataImport == null) {
            return redirect("/data-import");
        }
        List<UbrHouseholdImport> imports = taskService.getImportsBySessionIdForReview(dataImport.getId(), pageable);
        return view("targeting/import/review")
                .addObject("importSession", dataImport)
                .addObject("imports", imports);
    }

    @PostMapping("/merge")
    ModelAndView merge(
            @AuthenticationPrincipal String username,
            @Valid @ModelAttribute MergeInportsForm form,
            BindingResult bindingResult,
            @PathVariable("import-id") Long id, RedirectAttributes attributes) {
        DataImportView dataImport = getImport(id, attributes);
        if (dataImport == null) {
            return redirect("/data-import");
        }
        if (bindingResult.hasErrors()) {
            setDangerFlashMessage("Cannot merge data at this moment.", attributes);
            return redirectToReview(id);
        }
        try {
            dataImportService.mergeBatchIntoPopulation(dataImport);
            setSuccessFlashMessage("Data imported successfully", attributes);
            publishGeneralEvent("%s merged data in population from import session %s.", username, dataImport.getTitle());
            return redirect("/data-import");
        } catch (Exception e) {
            setDangerFlashMessage("There was an error when importing the data.", attributes);
            LOG.error("Exception during merge", e);
        }

        return redirectToReview(id);
    }

    @PostMapping("/delete")
    ModelAndView delete(
            @AuthenticationPrincipal String username,
            @Valid @ModelAttribute DeleteImportRecordForm form,
            BindingResult bindingResult,
            @PathVariable("import-id") Long id, RedirectAttributes attributes) {
        DataImportView dataImport = getImport(id, attributes);
        if (dataImport == null) {
            return redirect("/data-import");
        }
        if (bindingResult.hasErrors()) {
            setDangerFlashMessage("Cannot archive record at the moment.", attributes);
            return redirectToReview(id);
        }
        UbrHouseholdImport householdImport = taskService.getHouseholdImportByIdAndDataImportId(form.getId(), dataImport.getId());
        if (householdImport == null) {
            setDangerFlashMessage("Could not archive the selected record.", attributes);
        } else {
            householdImport.setArchived(true);
            taskService.saveHouseholdImport(householdImport);
            dataImportService.calculateImportSessionDuplicates(dataImport);
            publishGeneralEvent("%s archived record %s from import session %s.",
                    username, householdImport.toString(), dataImport.getTitle());
            setSuccessFlashMessage("Import record archived.", attributes);
        }
        return redirectToReview(id);
    }
}
