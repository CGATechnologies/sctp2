package org.cga.sctp.mis.targeting;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationCode;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.SelectOptionItem;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.targeting.EligibilityVerificationSession;
import org.cga.sctp.targeting.EligibilityVerificationSessionBase;
import org.cga.sctp.targeting.EligibilityVerificationSessionView;
import org.cga.sctp.targeting.TargetingService;
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
public class EligibilityVerificationController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private LocationService locationService;

    @AdminAndStandardAccessOnly
    @GetMapping
    public ModelAndView verification() {
        List<EligibilityVerificationSessionView> verificationList = targetingService.getVerificationSessionViews();
        return view("targeting/verification/history", "verifications", verificationList);
    }

    /*@GetMapping("/verification")
    public ModelAndView verification() {
        return view("targeting/verification", "results", completedResults);
    }*/

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
    public ModelAndView newVerificationSession(@ModelAttribute("form") NewVerificationSessionForm form) {
        return newModel();
    }

    @AdminAccessOnly
    @PostMapping
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

        targetingService.saveEligibilityVerificationSession(session);

        setSuccessFlashMessage("Pre-eligibility verification created", attributes);

        return redirect("/verification");
    }

    @AdminAndStandardAccessOnly
    @GetMapping("/review")
    public ModelAndView reviewEligibilityList(@RequestParam Long id) {
        return view("targeting/verification/review", "results", List.of());
    }
}
