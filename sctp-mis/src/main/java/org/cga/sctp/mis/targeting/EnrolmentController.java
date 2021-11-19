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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.beneficiaries.Individual;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.schools.SchoolService;
import org.cga.sctp.schools.SchoolsView;
import org.cga.sctp.targeting.*;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;
import org.cga.sctp.targeting.importation.parameters.Gender;
import org.cga.sctp.targeting.importation.parameters.GradeLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/targeting/enrolment")
public class EnrolmentController extends SecuredBaseController {

    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private SchoolService schoolService;


    @GetMapping
    public ModelAndView index() {

        return view("targeting/enrolment/sessions",
                "sessions",enrollmentService.getEnrollmentSessions());
    }

    @GetMapping("/households")
    public ModelAndView households(@RequestParam("session") Long sessionId, RedirectAttributes attributes, Pageable pageable) {
        EnrollmentSessionView sessionView = enrollmentService.getEnrollmentSession(sessionId);
        if (sessionView == null){
            setDangerFlashMessage("Enrollment session not found.", attributes);
            return redirect("/targeting/enrolment");
        }

        Slice<CbtRanking> rankedList = enrollmentService.getEnrolledHouseholds(sessionView, pageable);
        return view("targeting/enrolment/households")
                .addObject("sessionInfo",sessionView)
                .addObject("ranks", rankedList);
    }

    @GetMapping("/details")
    public ModelAndView details(@RequestParam("id") Long householdId,
                                @RequestParam("session") Long sessionId, RedirectAttributes attributes,
                                @ModelAttribute("enrollmentForm") EnrollmentForm enrollmentForm) {
        EnrollmentHousehold enrollmentHousehold = enrollmentService.findEnrollmentHousehold(sessionId,householdId);
        if (enrollmentHousehold == null){
            setDangerFlashMessage("Enrollment session not found.", attributes);
            return redirect("/targeting/enrolment/households?session="+sessionId);
        }

        HouseholdDetails householdDetails = enrollmentService.getHouseholdDetails(householdId);
        if (householdDetails == null){
            setDangerFlashMessage("Enrollment household not found.", attributes);
            return redirect("/targeting/enrolment/households?session="+sessionId);
        }

        List<Individual> individuals = beneficiaryService.getEligibleRecipients(householdId);
        List<Individual>  children = beneficiaryService.findSchoolChildren(householdId);
        List<SchoolsView> schools = schoolService.getSchools();


        String returnUrl = "households?session="+sessionId;

        return view("targeting/enrolment/details")
                .addObject("details",householdDetails)
                .addObject("gender", Gender.VALUES)
                .addObject("children", children)
                .addObject("educationLevel", EducationLevel.VALUES)
                .addObject("gradeLevel", GradeLevel.VALUES)
                .addObject("returnUrl", returnUrl)
                .addObject("schools",  schools)
                .addObject("individuals",individuals);
    }

//    @GetMapping("/test")
//    public void test() throws IOException {
//
//        String encodedString = "";
//        String outputFileName = "c://downloads//sctptest.jpeg";
//
//        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
//        FileUtils.writeByteArrayToFile(new File(outputFileName), decodedBytes);
//
//    }


//    @PostMapping("/save")
//    ModelAndView saveEnrollment(
//            @AuthenticationPrincipal String username,
//            HttpServletRequest request,
//            @Validated @ModelAttribute("enrollmentForm") EnrollmentForm enrollmentForm,
//            BindingResult result,
//            RedirectAttributes attributes){
//
//
//    }


    public void downloadFile(MultipartFile file,String fileName) throws IOException{
        //need to put path server path
        File convertFile = new File("D:\\user\\Pictures\\"+fileName);
        convertFile.createNewFile();
        FileOutputStream fout = new FileOutputStream(convertFile);
        fout.write(file.getBytes());
        fout.close();
    }


    @RequestMapping(value="/test", method= RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@RequestParam(required=true, value="file") MultipartFile file,
                                             @RequestParam(required=false, value="altPhoto") MultipartFile alternate,
                                             @RequestParam(required=true, value="jsondata")String jsondata)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        HouseholdRecipient householdRecipient = new HouseholdRecipient();

        EnrollmentForm enrollmentForm = objectMapper.readValue(jsondata, EnrollmentForm.class);

        String mainReceiverPhotoName = "main-"+enrollmentForm.getHouseholdId()+".jpg";
        String altReceiverPhotoName = null;
        downloadFile(file,mainReceiverPhotoName);




        if (enrollmentForm.getHasAlternate() != 0) {
            altReceiverPhotoName = "alt-"+enrollmentForm.getHouseholdId()+".jpg";
            downloadFile(alternate,altReceiverPhotoName);
            if (enrollmentForm.getNonHouseholdMember() != 0){
                enrollmentService.saveHouseholdAlternateRecipient(enrollmentForm.getHouseholdId(),
                        enrollmentForm.getMainReceiver(),
                        mainReceiverPhotoName,
                        altReceiverPhotoName,
                        enrollmentForm.getAltFirstName(),
                        enrollmentForm.getAltLastName(),
                        enrollmentForm.getAltNationalId(),
                        enrollmentForm.getAltGender(),
                        LocalDate.parse(enrollmentForm.getAltDOB()));
            }else{
                householdRecipient.setHouseholdId(enrollmentForm.getHouseholdId());
                householdRecipient.setMainRecipient(enrollmentForm.getMainReceiver());
                householdRecipient.setAltRecipient(enrollmentForm.getAltReceiver());
                householdRecipient.setCreatedAt(LocalDateTime.now());
                householdRecipient.setMainPhoto(mainReceiverPhotoName);
                householdRecipient.setAltPhoto(altReceiverPhotoName);
                enrollmentService.saveHouseholdRecipient(householdRecipient);
            }
        }else {
            householdRecipient.setHouseholdId(enrollmentForm.getHouseholdId());
            householdRecipient.setMainRecipient(enrollmentForm.getMainReceiver());
            householdRecipient.setAltRecipient(enrollmentForm.getAltReceiver());
            householdRecipient.setCreatedAt(LocalDateTime.now());
            householdRecipient.setMainPhoto(mainReceiverPhotoName);
            householdRecipient.setAltPhoto(altReceiverPhotoName);
            enrollmentService.saveHouseholdRecipient(householdRecipient);
        }


        List<SchoolEnrolled> schoolEnrolledList = new ArrayList<SchoolEnrolled>();
        List<SchoolEnrollmentForm> schoolEnrollmentForm = enrollmentForm.schoolEnrollmentForm;
        if (!schoolEnrollmentForm.isEmpty()){
            for(SchoolEnrollmentForm sch: schoolEnrollmentForm ) {
                schoolEnrolledList.add(new SchoolEnrolled(sch.getHouseholdId(), sch.getIndividualId(), sch.getEducationLevel(), sch.getGrade(), sch.getSchoolId(), sch.getStatus()));
            }
            enrollmentService.saveChildrenEnrolledSchool(schoolEnrolledList);
        }
        return new ResponseEntity<>("File is uploaded successfully", HttpStatus.OK);

    }

    @GetMapping("/edit")
    public ModelAndView edit(@RequestParam("id") Long householdId,
                                @RequestParam("session") Long sessionId, RedirectAttributes attributes,
                                @ModelAttribute("enrollmentForm") EnrollmentForm enrollmentForm) {

        EnrollmentHousehold enrollmentHousehold = enrollmentService.findEnrollmentHousehold(sessionId,householdId);
        if (enrollmentHousehold == null){
            setDangerFlashMessage("Enrollment session not found.", attributes);
            return redirect("/targeting/enrolment/households?session="+sessionId);
        }

        HouseholdDetails householdDetails = enrollmentService.getHouseholdDetails(householdId);
        if (householdDetails == null){
            setDangerFlashMessage("Enrollment household not found.", attributes);
            return redirect("/targeting/enrolment/households?session="+sessionId);
        }

        List<Individual> individuals = beneficiaryService.getEligibleRecipients(householdId);
        List<Individual>  children = beneficiaryService.findSchoolChildren(householdId);
        List<SchoolEnrolled> schoolEnrolled = enrollmentService.getSchoolEnrolledByHousehold(householdId);
        List<SchoolsView> schools = schoolService.getSchools();
        String returnUrl = "households?session="+sessionId;

        HouseholdRecipient householdRecipient =enrollmentService.getHouseholdRecipient(householdId);
        if (householdRecipient.getAltOther() != 0){
            AlternateRecipient alternateRecipient = enrollmentService.getHouseholdAlternateRecipient(householdRecipient.getAltOther());
            enrollmentForm.setAltGender(alternateRecipient.getGender());
            enrollmentForm.setAltDOB(alternateRecipient.getDateOfBirth().toString());
            enrollmentForm.setAltFirstName(alternateRecipient.getFirstName());
            enrollmentForm.setAltLastName(alternateRecipient.getLastName());
            enrollmentForm.setAltNationalId(alternateRecipient.getNationalId());
            System.out.println(alternateRecipient.getFirstName());
        }

        System.out.println(householdRecipient.getMainRecipient());
        enrollmentForm.setMainReceiver(householdRecipient.getMainRecipient());
        enrollmentForm.setAltReceiver(householdRecipient.getAltRecipient());
        enrollmentForm.setHasAlternate((householdRecipient.getAltOther() != 0 || householdRecipient.getAltRecipient() != 0) ? 1 : 0);
        enrollmentForm.setNonHouseholdMember((householdRecipient.getAltOther() != 0) ? 1 : 0);


        return view("targeting/enrolment/details")
                .addObject("details",householdDetails)
                .addObject("gender", Gender.VALUES)
                .addObject("children", children)
                .addObject("educationLevel", EducationLevel.VALUES)
                .addObject("gradeLevel", GradeLevel.VALUES)
                .addObject("returnUrl", returnUrl)
                .addObject("parent", 1)
                .addObject("schools",  schools)
                .addObject("schoolEnrolled", schoolEnrolled)
                .addObject("individuals",individuals);
    }


}
