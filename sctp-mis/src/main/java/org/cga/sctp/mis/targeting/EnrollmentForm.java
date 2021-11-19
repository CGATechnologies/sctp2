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

import java.util.List;

public class EnrollmentForm {

    private long mainReceiver;

    private long altReceiver;

  //  @DateTimeFormat(pattern = "yyyy-MM-dd")
  //  public LocalDate mainDOB;
//    private String mainDOB;

//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    public LocalDate altDOB;
    private String altDOB;

    private String altFirstName;

    public String getAltLastName() {
        return altLastName;
    }

    public void setAltLastName(String altLastName) {
        this.altLastName = altLastName;
    }

    private String altLastName;

//    private long mainGender;

    private int altGender;

    private int nonHouseholdMember;

    private long householdId;

    private int hasAlternate;

    public String getAltNationalId() {
        return altNationalId;
    }

    public void setAltNationalId(String altNationalId) {
        this.altNationalId = altNationalId;
    }

    private String altNationalId;

    public int getHasAlternate() {
        return hasAlternate;
    }

    public void setHasAlternate(int hasAlternate) {
        this.hasAlternate = hasAlternate;
    }

    public long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(long householdId) {
        this.householdId = householdId;
    }

    public long getMainReceiver() {
        return mainReceiver;
    }

    public void setMainReceiver(long mainReceiver) {
        this.mainReceiver = mainReceiver;
    }

    public long getAltReceiver() {
        return altReceiver;
    }

    public void setAltReceiver(long altReceiver) {
        this.altReceiver = altReceiver;
    }

    public String getAltDOB() {
        return altDOB;
    }

    public void setAltDOB(String altDOB) {
        this.altDOB = altDOB;
    }

    public String getAltFirstName() {
        return altFirstName;
    }

    public void setAltFirstName(String altName) {
        this.altFirstName = altName;
    }

    public int getAltGender() {
        return altGender;
    }

    public void setAltGender(int altGender) {
        this.altGender = altGender;
    }

    public int getNonHouseholdMember() {
        return nonHouseholdMember;
    }

    public void setNonHouseholdMember(int nonHouseholdMember) {
        this.nonHouseholdMember = nonHouseholdMember;
    }

    public List<SchoolEnrollmentForm> getSchoolEnrollmentForm() {
        return schoolEnrollmentForm;
    }

    public void setSchoolEnrollmentForm(List<SchoolEnrollmentForm> schoolEnrollmentForm) {
        this.schoolEnrollmentForm = schoolEnrollmentForm;
    }

    List<SchoolEnrollmentForm> schoolEnrollmentForm;


}
