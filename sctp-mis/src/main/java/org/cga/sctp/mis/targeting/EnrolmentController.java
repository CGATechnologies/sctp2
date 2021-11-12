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
import org.cga.sctp.targeting.*;
import org.cga.sctp.targeting.importation.parameters.Gender;
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

@Controller
@RequestMapping("/targeting/enrolment")
public class EnrolmentController extends SecuredBaseController {

    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    private BeneficiaryService beneficiaryService;


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

        Slice<Individual> individuals = beneficiaryService.getIndividualsForCommunityReview(householdId,null);
        return view("targeting/enrolment/details")
                .addObject("details",householdDetails)
                .addObject("gender", Gender.VALUES)
                .addObject("individuals",individuals.toList());
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
        File convertFile = new File("D:\\user\\Pictures\\"+fileName);
        convertFile.createNewFile();
        FileOutputStream fout = new FileOutputStream(convertFile);
        fout.write(file.getBytes());
        fout.close();
    }


    @RequestMapping(value="/test", method= RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadFile(@RequestParam(required=true, value="file") MultipartFile file,
                                             @RequestParam(required=true, value="altPhoto") MultipartFile alternate,
                                             @RequestParam(required=true, value="jsondata")String jsondata)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

         downloadFile(file,"main.jpg");
         downloadFile(alternate,"alter.jpg");
        EnrollmentForm enrollmentForm = objectMapper.readValue(jsondata, EnrollmentForm.class);
        System.out.println(enrollmentForm.getAltName());
        System.out.println(enrollmentForm.getMainReceiver());

        return new ResponseEntity<>("File is uploaded successfully", HttpStatus.OK);

    }

}
