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

package org.cga.sctp.targeting.enrollment;

import org.cga.sctp.targeting.importation.converters.GenderParameterValueConverter;
import org.cga.sctp.targeting.importation.parameters.Gender;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class HouseholdRecipientSummary {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Convert(converter = GenderParameterValueConverter.class)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "photo", length = 250)
    private String photo;

    @Column(name = "modified_at")
    private OffsetDateTime modifiedAt;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "age")
    private Long age;

    @Column(name = "individual_id", nullable = false, length = 50)
    private String individualId;

    @Column(name = "is_alternate", nullable = false)
    private Integer isAlternate;

    @Column(name = "is_household_member", nullable = false)
    private Boolean isHouseholdMember = false;

    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Column(name = "ml_code", nullable = false)
    private Long mlCode;

    @Column(name = "member_code", length = 16)
    private String memberCode;

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhoto() {
        return photo;
    }

    public OffsetDateTime getModifiedAt() {
        return modifiedAt;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Long getAge() {
        return age;
    }

    public String getIndividualId() {
        return individualId;
    }

    public Integer getIsAlternate() {
        return isAlternate;
    }

    public Boolean getIsHouseholdMember() {
        return isHouseholdMember;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public Long getMlCode() {
        return mlCode;
    }

    public String getMemberCode() {
        return memberCode;
    }
}