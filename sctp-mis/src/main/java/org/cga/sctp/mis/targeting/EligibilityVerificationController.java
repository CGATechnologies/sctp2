package org.cga.sctp.mis.targeting;

import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.beneficiaries.Household;
import org.cga.sctp.beneficiaries.Individual;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationCode;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.navigation.*;
import org.cga.sctp.mis.core.templating.SelectOptionItem;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.targeting.*;
import org.cga.sctp.targeting.criteria.Criterion;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/verification")
@BreadcrumbDefinition(module = ModuleNames.TARGETING, index = @BreadcrumbPath(link = "/verification", title = "Pre-Eligibility Verification", navigable = true))
public class EligibilityVerificationController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @AdminAndStandardAccessOnly
    @GetMapping
    public ModelAndView verification() {
        List<EligibilityVerificationSessionView> verificationList = targetingService.getVerificationSessionViews();
        return view("targeting/verification/history", "verifications", verificationList);
    }

    private List<SelectOptionItem> toSelectOptions(List<LocationCode> codes) {
        return codes.stream()
                .map(locationCode -> new SelectOptionItem(locationCode.getCode(),
                        locationCode.getName(), locationCode.getCode()))
                .collect(Collectors.toList());
    }

    private ModelAndView newModel() {
        List<Program> programs = programService.getActivePrograms();
        List<Criterion> criteria = targetingService.getActiveTargetingCriteria();
        return view("targeting/verification/new")
                .addObject("criteria", criteria)
                .addObject("programs", programs)
                .addObject("districts", toSelectOptions(locationService.getActiveDistrictCodes()));
    }

    @AdminAccessOnly
    @GetMapping("/new")
    @BreadcrumbPath(link = "/new", title = "New Eligibility Verification Session")
    public ModelAndView newVerificationSession(@ModelAttribute("form") NewVerificationSessionForm form) {
        return newModel();
    }

    @AdminAccessOnly
    @PostMapping
    @BreadcrumbPath(link = "/", title = "Create Eligibility Verification Session")
    public ModelAndView create(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute("form") NewVerificationSessionForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return newModel();
        }

        Program program = programService.getProgramById(form.getProgram());
        Criterion criterion = targetingService.getActiveTargetingCriterionById(form.getCriterion());
        Location district = locationService.findActiveLocationByCodeAndType(form.getDistrict(), LocationType.SUBNATIONAL1);
        Location ta = locationService.findActiveLocationByCodeAndType(form.getTraditionalAuthority(), LocationType.SUBNATIONAL2);

        if (program == null) {
            return setDangerMessage(newModel(), "Cannot find program.");
        }
        if (district == null) {
            return setDangerMessage(newModel(), "Cannot find district.");
        }
        if (ta == null) {
            return setDangerMessage(newModel(), "Cannot find traditional authority.");
        }

        if (!ta.getLocationType().isImmediateChildOf(district.getLocationType())) {
            return setDangerMessage(newModel(),
                    "Invalid location selection. Selected traditional authority does not belong to the selected district,.");
        }

        if (criterion == null) {
            return setDangerMessage(newModel(), "Selected criteria cannot be found.");
        }

        if (!criterion.isApplicable()) {
            return setDangerMessage(newModel(), "Selected criterion cannot be used because it does not contain filters.");
        }

        EligibilityVerificationSession session = new EligibilityVerificationSession();
        session.setHouseholds(0L);
        session.setUserId(user.id());
        session.setTaCode(ta.getCode());
        session.setProgramId(program.getId());
        session.setClusters(form.getClusters());
        session.setCriterionId(criterion.getId());
        session.setCreatedAt(LocalDateTime.now());
        session.setDistrictCode(district.getCode());
        session.setStatus(EligibilityVerificationSessionBase.Status.Review);

        // Run
        targetingService.addEligibilityVerificationSession(session, criterion, user.id());

        setSuccessFlashMessage("Pre-eligibility verification created", attributes);

        return redirect("/verification");
    }

    @AdminAndStandardAccessOnly
    @GetMapping("/review")
    @BreadcrumbPath(link = "/review", title = "Eligibility Verification Review")
    public ModelAndView reviewEligibilityList(@RequestParam Long id, RedirectAttributes attributes) {
        EligibilityVerificationSessionView verificationSessionView = targetingService.findVerificationViewById(id);
        if (verificationSessionView == null) {
            setDangerFlashMessage("Cannot find verification session.", attributes);
            return redirect("/verification");
        }
        List<EligibleHousehold> households = targetingService.getEligibleHouseholds(verificationSessionView);
        return view("targeting/verification/review", "households", households)
                .addObject("verification", verificationSessionView);
    }

    @AdminAndStandardAccessOnly
    @GetMapping("/hh-composition")
    @BreadcrumbPath(link = "/hh-composition", title = "Household Composition")
    public ModelAndView householdComposition(
            @RequestParam Long session,
            @RequestParam(name = "household_id") Long householdId,
            RedirectAttributes attributes) {
        Household household;
        List<Individual> individuals;
        EligibilityVerificationSessionView verificationSessionView;

        verificationSessionView = targetingService.findVerificationViewById(session);

        if (verificationSessionView == null) {
            setDangerFlashMessage("Cannot find verification session.", attributes);
            return redirect("/verification");
        }

        if ((household = beneficiaryService.findHouseholdById(householdId)) == null) {
            setDangerFlashMessage("Cannot find household", attributes);
            return redirect("/verification/review?id=" + session);
        }

        individuals = beneficiaryService.getHouseholdMembers(household.getHouseholdId());

        return view("targeting/verification/composition")
                .addObject("individuals", individuals)
                .addObject("verification", verificationSessionView)
                .addObject("household", household);
    }

    @AdminAccessOnly
    @PostMapping("/close")
    ModelAndView closeSession(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute("form") CloseVerificationSessionForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            setDangerFlashMessage("Something went wrong. Please try again.", attributes);
            return redirect("/verification");
        }

        EligibilityVerificationSession session = targetingService.getVerificationSessionById(form.getId());
        if (session == null) {
            setDangerFlashMessage("Cannot find pre-eligibility verification session.", attributes);
            return redirect("/verification");
        }

        if (!session.isOpen()) {
            setDangerFlashMessage("Cannot close this pre-eligibility verification session because it is already closed.", attributes);
            return redirect("/verification");
        }

        targetingService.closeVerificationSession(session, form.getDestination());

        if (session.getHouseholds() == 0) {
            setWarningFlashMessage("Pre-Eligibility verification session closed. However, there were no households that matched the targeting criteria.", attributes);
        } else {
            setSuccessFlashMessage(format("Pre-Eligibility verification closed and sent to %s",
                    form.getDestination()), attributes);
        }
        publishGeneralEvent("%s closed pre-eligibility verification session with id %d",
                user.username(), session.getId());

        return redirect("/verification");
    }
}
