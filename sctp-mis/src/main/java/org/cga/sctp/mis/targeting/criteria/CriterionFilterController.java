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

package org.cga.sctp.mis.targeting.criteria;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.SelectOptionItem;
import org.cga.sctp.targeting.TargetingService;
import org.cga.sctp.targeting.criteria.*;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/criteria/{criterion-id}/filters")
public class CriterionFilterController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    private Criterion findCriterionById(Long id, RedirectAttributes attributes) {
        Criterion criterion = targetingService.findCriterionById(id);
        if (criterion == null) {
            setDangerFlashMessage("Targeting criterion not found.", attributes);
        }
        return criterion;
    }

    private CriterionView findCriterionViewById(Long id, RedirectAttributes attributes) {
        CriterionView criterion = targetingService.findCriterionViewById(id);
        if (criterion == null) {
            setDangerFlashMessage("Targeting criterion not found.", attributes);
        }
        return criterion;
    }

    private ModelAndView redirectToCriteriaList() {
        return redirect("/criteria");
    }

    private ModelAndView redirectToCriteriaFilters(Long criterionId) {
        return redirect(format("/criteria/%d/filters", criterionId));
    }

    @GetMapping
    @AdminAndStandardAccessOnly
    ModelAndView list(@PathVariable("criterion-id") Long id, RedirectAttributes attributes) {
        Criterion criterion = findCriterionById(id, attributes);
        if (criterion == null) {
            return redirectToCriteriaList();
        }
        List<CriteriaFilterView> filterList = targetingService.getFilterViewsByCriterionId(criterion.getId());
        return view("targeting/criteria/filters/index")
                .addObject("criterion", criterion)
                .addObject("filters", filterList);
    }

    @GetMapping("/new")
    @AdminAccessOnly
    ModelAndView newFilter(@PathVariable("criterion-id") Long id, RedirectAttributes attributes) {
        Long usageCount;
        Criterion criterion = findCriterionById(id, attributes);
        if (criterion == null) {
            return redirectToCriteriaList();
        }

        if ((usageCount = targetingService.getCriterionUsageCount(criterion)) >= 1) {
            setDangerFlashMessage(
                    format(
                            "This targeting criteria can longer be modified because it has already been " +
                                    "applied to %,d pre-eligibility verification run(s).",
                            usageCount
                    ),
                    attributes
            );
            return redirectToCriteriaFilters(criterion.getId());
        }

        List<CriteriaFilterView> filterList = targetingService.getFilterViewsByCriterionId(criterion.getId());
        return view("targeting/criteria/filters/new")
                .addObject("criterion", criterion)
                .addObject("filters", filterList)
                .addObject("conjunctions", CriteriaFilterObject.Conjunction.VALUES)
                .addObject("templates", Collections.emptyList());
    }

    @GetMapping(value = "/get-filter-parameters", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAccessOnly
    ResponseEntity<List<SelectOptionItem>> getFilterParameters(@RequestParam("category") String category) {
        List<SelectOptionItem> optionItemList;
        List<CriteriaFilterTemplate> templates;
        switch (category) {
            case "individual" -> templates = targetingService.getIndividualFilterTemplates();
            case "household" -> templates = targetingService.getHouseholdFilterTemplates();
            default -> templates = Collections.emptyList();
        }

        if (!templates.isEmpty()) {
            optionItemList = templates
                    .stream()
                    .map(template -> new SelectOptionItem(
                            template.getId(),
                            template.getLabel(),
                            Map.of(
                                    "type", template.getFieldType(),
                                    "category", template.getTableName(),
                                    "values", template.getFieldValues(),
                                    "hint", template.getHint()
                            )
                    ))
                    .collect(Collectors.toList());
        } else {
            optionItemList = Collections.emptyList();
        }

        return ResponseEntity.ok(optionItemList);
    }

    @GetMapping(value = "/get-template-options", produces = MediaType.APPLICATION_JSON_VALUE)
    @AdminAccessOnly
    ResponseEntity<List<SelectOptionItem>> getFilterTemplateOptions(@RequestParam("template") Long templateId) {
        List<SelectOptionItem> optionItemList;
        CriteriaFilterTemplate template;
        List<FilterTemplateListOption> listOptions;

        if ((template = targetingService.findFilterTemplateById(templateId)) == null) {
            return ResponseEntity.ok().build();
        }

        listOptions = targetingService.getFilterTemplateListOptions(templateId);

        if (!listOptions.isEmpty()) {
            optionItemList = listOptions
                    .stream()
                    .map(option -> new SelectOptionItem(option.getId(), option.getFieldValue(), option.getFieldText()))
                    .collect(Collectors.toList());
        } else {
            optionItemList = Collections.emptyList();
        }

        return ResponseEntity.ok(optionItemList);
    }

    @PostMapping
    @AdminAccessOnly
    ModelAndView add(
            @AuthenticationPrincipal String username,
            @PathVariable("criterion-id") Long id,
            @Valid @ModelAttribute("form") AddCriteriaFilterForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        Long usageCount;
        CriteriaFilterTemplate template;
        Criterion criterion = findCriterionById(id, attributes);

        if (criterion == null) {
            return redirectToCriteriaList();
        }

        if (result.hasErrors()) {
            return withDangerMessage("targeting/criteria/filters/new", "Something went wrong while saving your filter.")
                    .addObject("criterion", criterion)
                    .addObject("conjunctions", CriteriaFilterObject.Conjunction.VALUES)
                    .addObject("templates", Collections.emptyList());
        }

        if ((usageCount = targetingService.getCriterionUsageCount(criterion)) >= 1) {
            setDangerFlashMessage(
                    format(
                            "This targeting criteria can longer be modified because it has already been " +
                                    "applied to %,d pre-eligibility verification run(s).",
                            usageCount
                    ),
                    attributes
            );
            return redirectToCriteriaFilters(criterion.getId());
        }

        if ((template = targetingService.findFilterTemplateById(form.getTemplateId())) == null) {
            return withDangerMessage("targeting/criteria/filters/new", "Selected filter template does not exist.")
                    .addObject("criterion", criterion)
                    .addObject("conjunctions", CriteriaFilterObject.Conjunction.VALUES)
                    .addObject("templates", Collections.emptyList());
        }

        if (form.getConjunction() == CriteriaFilterObject.Conjunction.None) {
            if (targetingService.getCriterionFilterCount(criterion) > 1) {
                return withDangerMessage("targeting/criteria/filters/new",
                        "The conjunction ’None’ can only be used by the first filter only.")
                        .addObject("criterion", criterion)
                        .addObject("conjunctions", CriteriaFilterObject.Conjunction.VALUES)
                        .addObject("templates", Collections.emptyList());
            }
        }

        CriteriaFilter filter = new CriteriaFilter();
        filter.setFilterValue(form.getValue());
        filter.setTemplateId(template.getId());
        filter.setCriterionId(criterion.getId());
        filter.setConjunction(form.getConjunction());

        targetingService.saveCriteriaFilter(filter);
        targetingService.compileFilterQuery(criterion);

        setSuccessFlashMessage("Filter added successfully", attributes);
        return redirectToCriteriaFilters(criterion.getId());
    }

    @PostMapping("/remove")
    @AdminAccessOnly
    ModelAndView remove(
            @PathVariable("criterion-id") Long id,
            @Valid @ModelAttribute("form") RemoveCriteriaFilterForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        Long usageCount;
        Criterion criterion = findCriterionById(id, attributes);

        if (criterion == null) {
            return redirectToCriteriaList();
        }

        if (result.hasErrors()) {
            setDangerFlashMessage("Something went wrong. Please try again.", attributes);
            return redirectToCriteriaFilters(id);
        }

        if ((usageCount = targetingService.getCriterionUsageCount(criterion)) >= 1) {
            setDangerFlashMessage(
                    format(
                            "This targeting criteria can longer be modified because it has already been " +
                                    "applied to %,d pre-eligibility verification run(s).",
                            usageCount
                    ),
                    attributes
            );
            return redirectToCriteriaFilters(criterion.getId());
        }

        CriteriaFilter filter = targetingService.findCriteriaFilterById(form.getFilter(), id);
        if (filter == null) {
            setDangerFlashMessage("Filter does not exist", attributes);
            return redirectToCriteriaFilters(id);
        }
        targetingService.deleteCriteriaFilter(filter);
        targetingService.compileFilterQuery(criterion);

        setSuccessFlashMessage("Filter removed", attributes);
        return redirectToCriteriaFilters(id);
    }

}
