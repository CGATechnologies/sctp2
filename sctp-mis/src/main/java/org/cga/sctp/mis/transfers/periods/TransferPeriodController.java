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

import org.apache.commons.lang3.StringUtils;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.targeting.enrollment.EnrollmentService;
import org.cga.sctp.targeting.enrollment.HouseholdEnrollmentData;
import org.cga.sctp.transfers.TransferException;
import org.cga.sctp.transfers.TransferService;
import org.cga.sctp.transfers.TransferView;
import org.cga.sctp.transfers.TransferViewRepository;
import org.cga.sctp.transfers.agencies.TransferAgencyService;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.cga.sctp.transfers.periods.TransferPeriodException;
import org.cga.sctp.transfers.periods.TransferPeriodService;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transfers/periods")
public class TransferPeriodController extends BaseController {
    @Autowired
    private ProgramService programService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TransferAgencyService transferAgencyService;

    @Autowired
    private TransferPeriodService transferPeriodService;

    @Autowired
    private TransferViewRepository transferViewRepository;

    @Autowired
    private TransferService transferService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView index() {
        List<TransferPeriod> transferPeriodList = transferPeriodService.findAll();
        return view("/transfers/periods/index")
                .addObject("transferPeriods", transferPeriodList);
    }

    @GetMapping("/open-new")
    @AdminAndStandardAccessOnly
    public ModelAndView viewCreateTransferPeriod(@RequestParam(value = "district-id", required = false) Long districtId) {
        Location district = null;
        TransferPeriod lastPeriod = null;
        if (districtId != null) {
            district = locationService.findById(districtId);
//            lastPeriod = transferPeriodService.findLastPeriodInLocation(districtId);
        }
        // TODO: open a period with a session!
        List<Location> districts = locationService.getActiveDistricts();
        List<Program> programs = programService.getActivePrograms();
        return view("/transfers/periods/new")
                .addObject("programs", programs)
                .addObject("districts", districts)
                .addObject("district", district)
                .addObject("lastPeriod", lastPeriod);
    }

    @PostMapping("/open-new")
    @AdminAndStandardAccessOnly
    public ResponseEntity<?> handleCreateTransferPeriod(@AuthenticatedUserDetails AuthenticatedUser user,
                                                        @Validated @RequestBody TransferPeriodForm form) {

        TransferPeriod newPeriod = new TransferPeriod();
        Location district = locationService.findByCode(form.getDistrictCode());
        if (district == null) {
            return ResponseEntity.notFound().build();
        }

        newPeriod.setStartDate(form.getStartDate());
        newPeriod.setEndDate(form.getEndDate());
        String name = String.format("%s: %s - %s", district.getName(), form.getStartDate().format(DateTimeFormatter.ISO_DATE), form.getEndDate().format(DateTimeFormatter.ISO_DATE));
        newPeriod.setName(name);
        newPeriod.setDescription(name);
        newPeriod.setProgramId(form.getProgramId());
        newPeriod.setDistrictCode(form.getDistrictCode());
        newPeriod.setOpenedBy(user.id());
        //newPeriod.setTransferSessionId(form.getTransferSessionId());

        String villageClusterCodes = convertToLocationCodesString(form.getVillageClusterCodes());
        newPeriod.setVillageClusterCodes(villageClusterCodes);

        String traditionalAuthorityCodes = convertToLocationCodesString(form.getTraditionalAuthorityCodes());
        newPeriod.setTraditionalAuthorityCodes(traditionalAuthorityCodes);

        TransferPeriod transferPeriod = transferPeriodService.openNewPeriod(newPeriod);
        if (transferPeriod == null) {
            return ResponseEntity.notFound().build();
        }

        publishGeneralEvent("User %s opened a new Transfer Period in district: %s", user.username(), form.getDistrictCode());
//            return redirect(String.format("/transfers/periods/in-districts/%d", form.getDistrictId()));

        return ResponseEntity.ok(transferPeriod);
    }

    @GetMapping("/delete/{period-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getDeletePeriod(@AuthenticatedUserDetails AuthenticatedUser user,
                                        @PathVariable("period-id") Long periodId,
                                        RedirectAttributes attributes) {

        Optional<TransferPeriod> transferPeriod = transferPeriodService.findById(periodId);
        if (transferPeriod.isEmpty()) {
            // TODO: flash messsage that the period does not exist...
            return redirect("/transfers/periods");
        }

        return view("/transfers/periods/delete")
                .addObject("transferPeriod", transferPeriod.get());
    }

    @PostMapping("/delete/{period-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView postDeletePeriod(@AuthenticatedUserDetails AuthenticatedUser user,
                                         @PathVariable("period-id") Long periodId,
                                         RedirectAttributes attributes) {

        transferPeriodService.deletePeriod(periodId);
        publishGeneralEvent("User %s deleted an open period with id %s", user.username(), periodId);
        return redirect("/transfers/periods");
    }

    @GetMapping("/calculate-transfers/{period-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getCalculatePage(@AuthenticatedUserDetails AuthenticatedUser user,
                                         @PathVariable("period-id") Long transferPeriodId,
                                         RedirectAttributes attributes) {

        Optional<TransferPeriod> transferPeriod = transferPeriodService.findById(transferPeriodId);
        if (transferPeriod.isEmpty()) {
            return redirectWithDangerMessageModelAndView("/transfers/periods", "Transfer Period does not exist or cannot calculate transfers for it", attributes);
        }

        return view("/transfers/periods/calculate-transfers")
                .addObject("transferPeriod", transferPeriod.get());
    }

    @PostMapping("/initiate-calculations/{period-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<Object> postCalculatePage(@AuthenticatedUserDetails AuthenticatedUser user,
                                                    @PathVariable("period-id") Long transferPeriodId) {
        Optional<TransferPeriod> transferPeriod = transferPeriodService.findById(transferPeriodId);
        if (transferPeriod.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            logger.info("Checking for enrollment session in District {}", transferPeriod.get().getDistrictCode());
            Location location = locationService.findByCode(transferPeriod.get().getDistrictCode());

            var enrollmentSession = enrollmentService.findMostRecentSessionByLocation(location.getCode());
            if (enrollmentSession.isEmpty()) {
                // TODO:
                throw new TransferException("cannot initiate transfers in period when no enrollment sessions are available");
            }
            Page<HouseholdEnrollmentData> households = enrollmentService.getAllHouseholdEnrollmentData(enrollmentSession.get().getId());

            transferService.createTransfers(transferPeriod.get(), user.id(), households.toList());
            // TODO: return data or calculate off-thread
            // TODO: change return type of this API
            return ResponseEntity.ok(transferPeriod.get());
        } catch (Exception e ) {
            LOG.error("Failed to create transfers records", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reconcile/{period-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getReconcilePage(@AuthenticatedUserDetails AuthenticatedUser user,
                                         @PathVariable("period-id") Long periodId,
                                         RedirectAttributes attributes) {
        Optional<TransferPeriod> transferPeriod = transferPeriodService.findById(periodId);
        if (transferPeriod.isEmpty()) {
            return redirectWithDangerMessageModelAndView("/transfers/periods", "Transfer Period does not exist", attributes);
        }

        return view("/transfers/periods/reconcile")
                .addObject("transferPeriod", transferPeriod.get());
    }

    @GetMapping("/transfer-list/{period-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<List<TransferView>> getTransferListPage(@AuthenticatedUserDetails AuthenticatedUser user,
                                                                  @PathVariable("period-id") Long periodId,
                                                                  Pageable pageable) {
        Optional<TransferPeriod> transferPeriod = transferPeriodService.findById(periodId);
        if (transferPeriod.isEmpty()) {
//            return redirectWithDangerMessageModelAndView("/transfers/periods", "Transfer Period does not exist", attributes);
            return ResponseEntity.notFound().build();
        }

        List<TransferView> transferViewList = transferViewRepository.findAllByPeriodByLocationToDistrictLevel(
                transferPeriod.get().getId(),
                transferPeriod.get().getDistrictCode(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return ResponseEntity.ok(transferViewList);
    }

    @GetMapping("/close/{period-id}")
    @AdminAndStandardAccessOnly
    public ModelAndView getClosePage(@AuthenticatedUserDetails AuthenticatedUser user,
                                         @PathVariable("period-id") Long periodId,
                                         RedirectAttributes attributes) {
        Optional<TransferPeriod> transferPeriod = transferPeriodService.findById(periodId);
        if (transferPeriod.isEmpty()) {
            return redirectWithDangerMessageModelAndView("/transfers/periods", "Transfer Period does not exist", attributes);
        }

        return view("/transfers/periods/close")
                .addObject("transferPeriod", transferPeriod.get());
    }


    @GetMapping("/close")
    @AdminAndStandardAccessOnly
    public ModelAndView viewCloseTransferPeriod() {
        return view("/transfers/periods/close");
    }

    @GetMapping("/view/{id}")
    @AdminAndStandardAccessOnly
    public ModelAndView viewTransferPeriod(@PathVariable("id") long id) {
        Optional<TransferPeriod> transferPeriod = transferPeriodService.findById(id);
        if (transferPeriod.isEmpty()) {
            return redirect("/transfers/periods");
        }
        return view("/transfers/periods/view")
                .addObject("transferPeriod", transferPeriod.get());
    }

    @GetMapping("/{period-id}")
    @AdminAndStandardAccessOnly
    public ResponseEntity<TransferPeriod> getDeletePeriod(@AuthenticatedUserDetails AuthenticatedUser user,
                                        @PathVariable("period-id") Long periodId) {

        TransferPeriod transferPeriod = transferPeriodService.findById(periodId)
                .orElseThrow(() -> new TransferPeriodException("Transfer period with id: " + periodId + " not found"));

        return ResponseEntity.ok(transferPeriod);
    }

    @GetMapping("/in-district/{district-code}")
    @AdminAndStandardAccessOnly
    public ModelAndView viewGetTransferPeriodsInDistrict(@PathVariable("district-code") Long districtCode) {
        Location district = locationService.findById(districtCode);
        List<TransferPeriod> transferPeriods = transferPeriodService.findAllByDistrictCode(districtCode);

        return view("transfers/periods/list_by_district")
                .addObject("district", district)
                .addObject("transferPeriods", transferPeriods);
    }

    private String convertToLocationCodesString(List<Long> codes) {
        for (Long code : codes) {
            if (!locationService.isValidLocationCode(code)) {
                throw new TransferPeriodException("Location with code: " + code + " not found");
            }
        }

        return StringUtils.join(codes, ",");
    }
}
