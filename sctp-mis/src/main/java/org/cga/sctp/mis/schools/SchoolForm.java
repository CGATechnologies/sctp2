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

package org.cga.sctp.mis.schools;

import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.schools.School;
import org.cga.sctp.targeting.importation.parameters.EducationLevel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

public class SchoolForm {
    private Long id;
    @NotEmpty(message = "Code cannot be empty")
    private String code;

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Contact name cannot be empty")
    private String contactName;

    @NotEmpty(message = "Contact Phone cannot be empty")
    private String contactPhone;

    @NotNull(message = "Education Level must be specified")
    private EducationLevel educationLevel;

    private Long ta;

    private Long districtId;

    @NotNull(message = "Education Zone must be specified")
    private Long educationZoneId;

    @NotNull(message = "Active flag must be set.")
    private Booleans active;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Long getTa() {
        return ta;
    }

    public void setTa(Long ta) {
        this.ta = ta;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getEducationZoneId() {
        return educationZoneId;
    }

    public void setEducationZoneId(Long educationZone) {
        this.educationZoneId = educationZone;
    }

    public Booleans getActive() {
        return active;
    }

    public void setActive(Booleans active) {
        this.active = active;
    }

    public School toSchoolObject(Optional<School> existingSchool) {
        SchoolForm schoolForm = this;
        School school = existingSchool.orElse(new School());
        school.setName(schoolForm.getName());
        school.setCode(schoolForm.getCode());
        school.setEducationLevel(schoolForm.getEducationLevel());
        // school.setEducationZone(schoolForm.getEducationZone());
        school.setContactName(schoolForm.getContactName());
        school.setContactPhone(schoolForm.getContactPhone());
        school.setModifiedAt(LocalDateTime.now());
        school.setActive(schoolForm.getActive().value);

        return school;
    }

    public void fillFieldsFromSchool(School school) {
        this.setId(school.getId());
        this.setName(school.getName());
        this.setCode(school.getCode());
        this.setEducationZoneId(school.getEducationZoneId());
        this.setEducationLevel(school.getEducationLevel());
        this.setContactName(school.getContactName());
        this.setContactPhone(school.getContactPhone());
        this.setActive(Booleans.of(school.getActive()));
    }
}
