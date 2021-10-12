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

package org.cga.sctp.beneficiaries;

import org.cga.sctp.targeting.importation.converters.*;
import org.cga.sctp.targeting.importation.parameters.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "individuals")
public class Individual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private boolean isEstimatedDob;
    private String locationCode;

    private Long ubrCsvBatchNumber;

    @Convert(converter = MortalityStatusParameterValueConverter.class)
    private MortalityStatus status;
    @Convert(converter = GenderParameterValueConverter.class)
    private Gender gender;
    @Convert(converter = MaritalStatusParameterValueConverter.class)
    private MaritalStatus maritalStatus;
    private String individualId;
    private LocalDate idIssueDate;
    private LocalDate idExpiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
    private Long householdId;
    private boolean deleted;
    private String phoneNumber;
    @Convert(converter = DisabilityParameterValueConverter.class)
    private Disability disability;
    @Convert(converter = ChronicIllnessParameterValueConverter.class)
    private ChronicIllness chronicIllness;
    private boolean fitForWork;

    @Column(name = "orphan_status")
    @Convert(converter = OrphanStatusParameterValueConverter.class)
    private Orphanhood orphanhood;

    @Convert(converter = EducationLevelParameterValueConverter.class)
    private EducationLevel highestEducationLevel;

    @Convert(converter = GradeLevelParameterValueConverter.class)
    private GradeLevel gradeLevel;

    private String schoolName;
    private String sourcedFrom;

    @Convert(converter = RelationshipToHeadParameterValueConverter.class)
    private RelationshipToHead relationshipToHead;

    @Column(name = "ubr_household_member_id")
    private Long urbMemberId;

    public Long getUbrCsvBatchNumber() {
        return ubrCsvBatchNumber;
    }

    public void setUbrCsvBatchNumber(Long ubrCsvBatchNumber) {
        this.ubrCsvBatchNumber = ubrCsvBatchNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isEstimatedDob() {
        return isEstimatedDob;
    }

    public void setEstimatedDob(boolean estimatedDob) {
        isEstimatedDob = estimatedDob;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public MortalityStatus getStatus() {
        return status;
    }

    public void setStatus(MortalityStatus status) {
        this.status = status;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getIndividualId() {
        return individualId;
    }

    public void setIndividualId(String individualId) {
        this.individualId = individualId;
    }

    public LocalDate getIdIssueDate() {
        return idIssueDate;
    }

    public void setIdIssueDate(LocalDate idIssueDate) {
        this.idIssueDate = idIssueDate;
    }

    public LocalDate getIdExpiryDate() {
        return idExpiryDate;
    }

    public void setIdExpiryDate(LocalDate idExpiryDate) {
        this.idExpiryDate = idExpiryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Disability getDisability() {
        return disability;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
    }

    public ChronicIllness getChronicIllness() {
        return chronicIllness;
    }

    public void setChronicIllness(ChronicIllness chronicIllness) {
        this.chronicIllness = chronicIllness;
    }

    public boolean isFitForWork() {
        return fitForWork;
    }

    public void setFitForWork(boolean fitForWork) {
        this.fitForWork = fitForWork;
    }

    public Orphanhood getOrphanhood() {
        return orphanhood;
    }

    public void setOrphanhood(Orphanhood orphanhood) {
        this.orphanhood = orphanhood;
    }

    public EducationLevel getHighestEducationLevel() {
        return highestEducationLevel;
    }

    public void setHighestEducationLevel(EducationLevel highestEducationLevel) {
        this.highestEducationLevel = highestEducationLevel;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSourcedFrom() {
        return sourcedFrom;
    }

    public void setSourcedFrom(String sourcedFrom) {
        this.sourcedFrom = sourcedFrom;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public RelationshipToHead getRelationshipToHead() {
        return relationshipToHead;
    }

    public void setRelationshipToHead(RelationshipToHead relationshipToHead) {
        this.relationshipToHead = relationshipToHead;
    }

    public Long getUrbMemberId() {
        return urbMemberId;
    }

    public void setUrbMemberId(Long urbMemberId) {
        this.urbMemberId = urbMemberId;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }
}
