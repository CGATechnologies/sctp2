package org.cga.sctp.mis.targeting;

import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.beneficiaries.Household;
import org.cga.sctp.beneficiaries.Individual;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.targeting.*;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.cga.sctp.mis.location.LocationCodeUtil.toSelectOptions;

@Controller
@RequestMapping("/targeting/community")
public class CommunityBasedTargetingController extends BaseController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @AdminAccessOnly
    @GetMapping("/new")
    public ModelAndView form(@ModelAttribute("form") NewTargetingSessionForm form) {
        return view("targeting/community/new")
                .addObject("programs", programService.getActivePrograms())
                .addObject("clusters", List.of())
                .addObject("authorities", List.of())
                .addObject("districts", toSelectOptions(locationService.getActiveDistrictCodes()));
    }

    private Location getActiveLocationByCode(Long id, LocationType type, String message, RedirectAttributes attributes) {
        Location location = locationService.findActiveLocationByCodeAndType(id, type);
        if (location == null) {
            setDangerFlashMessage(message, attributes);
        }
        return location;
    }

    @AdminAccessOnly
    @PostMapping("/new-session")
    ModelAndView newSession(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute("form") NewTargetingSessionForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        Program program;
        Location cluster;
        Location district;
        Location traditionalAuthority;
        TargetingSession targetingSession;

        if (result.hasErrors()) {
            return view("targeting/community/new")
                    .addObject("programs", programService.getActivePrograms())
                    .addObject("clusters", List.of())
                    .addObject("authorities", List.of())
                    .addObject("districts", toSelectOptions(locationService.getActiveDistrictCodes()));
        }
        if ((program = programService.getActiveProgramById(form.getProgram())) == null) {
            setDangerFlashMessage("Selected program does not exist.", attributes);
            return redirect("/targeting/community");
        }

        district = getActiveLocationByCode(form.getDistrict(),
                LocationType.SUBNATIONAL1, "District does not exist", attributes);

        traditionalAuthority = getActiveLocationByCode(form.getTraditionalAuthority(),
                LocationType.SUBNATIONAL2, "T.A does not exist", attributes);

        if (district == null || traditionalAuthority == null) {
            return redirect("/targeting/community");
        }

        if (!traditionalAuthority.getLocationType().isImmediateChildOf(district.getLocationType())) {
            setDangerFlashMessage("Invalid location selection.", attributes);
            return redirect("/targeting/community");
        }

        targetingSession = new TargetingSession();
        targetingSession.setCreatedBy(user.id());
        targetingSession.setProgramId(form.getProgram());
        targetingSession.setClusters(form.getClusters());
        targetingSession.setDistrictCode(form.getDistrict());
        targetingSession.setCreatedAt(LocalDateTime.now());
        targetingSession.setStatus(TargetingSession.SessionStatus.Review);
        targetingSession.setTaCode(form.getTraditionalAuthority());
        targetingSession.setMeetingPhase(TargetingSessionBase.MeetingPhase.second_community_meeting);

        targetingService.saveTargetingSession(targetingSession);
        targetingService.performCommunityBasedTargetingRanking(targetingSession);

        publishGeneralEvent("%s created community based targeting session for %s",
                user.username(), program.getName());

        setSuccessFlashMessage("Community based targeting session created", attributes);
        return redirect("/targeting/community");
    }

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView community() {
        return view("targeting/community/sessions",
                "sessions", targetingService.targetingSessionViewList());
    }


    @GetMapping("/review")
    @AdminAndStandardAccessOnly
    ModelAndView details(@RequestParam("session") Long sessionId, RedirectAttributes attributes, Pageable pageable) {
        TargetingSessionView session = targetingService.findTargetingSessionViewById(sessionId);
        if (session == null) {
            setDangerFlashMessage("Community based targeting session not found.", attributes);
            return redirect("/targeting/community");
        }
        // TODO Add pagination controls to view
        Slice<CbtRankingResult> rankedList = targetingService.getCbtRanking(session, pageable);
        return view("targeting/community/details")
                .addObject("isSessionOpen", session.isOpen())
                .addObject("ranks", rankedList)
                .addObject("targetingSession", session);
    }

    @GetMapping("/composition")
    @AdminAndStandardAccessOnly
    ModelAndView householdComposition(
            @RequestParam("session") Long sessionId,
            @RequestParam("id") Long householdId,
            RedirectAttributes attributes,
            Pageable pageable) {

        TargetingSessionView session = targetingService.findTargetingSessionViewById(sessionId);
        if (session == null) {
            setDangerFlashMessage("Community based targeting session not found.", attributes);
            return redirect("/targeting/community");
        }

        Household household = beneficiaryService.findHouseholdByTargetingSessionIdAndHouseholdId(sessionId, householdId);
        if (household == null) {
            setDangerFlashMessage("Household not found.", attributes);
            return redirect("/targeting/community");
        }

        Slice<Individual> individuals = beneficiaryService.getIndividualsForCommunityReview(householdId, pageable);
        return view("targeting/community/composition")
                .addObject("individuals", individuals)
                .addObject("household", household)
                .addObject("targetingSession", session);
    }

    @PostMapping("/close")
    @AdminAccessOnly
    ModelAndView closeSession(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute CloseCbtSessionForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            setDangerFlashMessage("Cannot close session at the moment. Please try again.", attributes);
            return redirect("/targeting/community");
        }

        TargetingSessionView session = targetingService.findTargetingSessionViewById(form.getId());
        if (session == null) {
            setDangerFlashMessage("Cannot find session", attributes);
            return redirect("/targeting/community");
        }
        if (session.isClosed()) {
            setDangerFlashMessage("Cannot close session. Session is already closed.", attributes);
            return redirect("/targeting/community");
        }

        targetingService.closeTargetingSession(session, user.id());

        publishGeneralEvent("%s closed community based targeting session for %s that was opened by %s",
                user.username(), session.getProgramName(), session.getCreatorName());

        setSuccessFlashMessage("Targeting session closed.", attributes);
        return redirect("/targeting/community/review?session=" + session.getId());
    }

    @PostMapping("/remove-household")
    @AdminAccessOnly
    ModelAndView removeHousehold(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute RemoveHouseholdFromCbtSessionForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            setDangerFlashMessage("Cannot complete this request at the moment. Please try again.", attributes);
            return redirect("/targeting/community");
        }

        TargetingResult targetingResult;
        TargetingSessionView session = targetingService.findTargetingSessionViewById(form.getSession());

        if (session == null) {
            setDangerFlashMessage("Cannot find session", attributes);
            return redirect("/targeting/community");
        }

        if (session.isClosed()) {
            setDangerFlashMessage("Cannot make changes to an already closed session.", attributes);
            return redirect("/targeting/community/review?session=" + session.getId());
        }

        targetingResult = targetingService.findTargetingResultByHouseholdId(session.getId(), form.getHousehold());

        if (targetingResult == null) {
            setDangerFlashMessage("Cannot find selected household.", attributes);
            return redirect("/targeting/community/review?session=" + session.getId());
        }

        targetingResult.setStatus(CbtStatus.Ineligible);

        targetingService.saveTargetingResult(targetingResult);

        publishGeneralEvent("%s marked household with %d as ineligible. Session id = %d",
                user.username(), targetingResult.getHousehold(), session.getId());

        setSuccessFlashMessage("Household marked as ineligible.", attributes);
        return redirect("/targeting/community/review?session=" + session.getId());
    }

    @PostMapping("/change-rank")
    @AdminAccessOnly
    ModelAndView changeHouseholdRank(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute ChangeHouseholdRankForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        Integer oldRank;

        if (result.hasErrors()) {
            setDangerFlashMessage("Cannot complete this request at the moment. Please try again.", attributes);
            return redirect("/targeting/community");
        }

        TargetingResult targetingResult;
        TargetingSessionView session = targetingService.findTargetingSessionViewById(form.getSession());

        if (session == null) {
            setDangerFlashMessage("Cannot find session", attributes);
            return redirect("/targeting/community");
        }

        if (session.isClosed()) {
            setDangerFlashMessage("Cannot make changes to an already closed session.", attributes);
            return redirect("/targeting/community/review?session=" + session.getId());
        }

        if (form.getRank() <= 0) {
            setDangerFlashMessage("Rank value must start from 1.", attributes);
            return redirect("/targeting/community/review?session=" + session.getId());
        }

        targetingResult = targetingService.findTargetingResultByHouseholdId(session.getId(), form.getHousehold());

        if (targetingResult == null) {
            setDangerFlashMessage("Cannot find selected household.", attributes);
            return redirect("/targeting/community/review?session=" + session.getId());
        }

        oldRank = targetingResult.getRanking();

        targetingResult.setRanking(form.getRank());
        targetingService.saveTargetingResult(targetingResult);

        publishGeneralEvent("%s changed household(%d) rank from %d to %d.",
                user.username(), targetingResult.getHousehold(), oldRank, form.getRank());

        setSuccessFlashMessage("Household rank changed.", attributes);
        return redirect("/targeting/community/review?session=" + session.getId());
    }
}
