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

package org.cga.sctp.targeting.importation;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.conversions.DateConversion;
import com.univocity.parsers.conversions.EnumSelector;
import org.cga.sctp.beneficiaries.CommonHouseholdAttributes;
import org.cga.sctp.targeting.importation.conversion.CollectionConversion;
import org.cga.sctp.targeting.importation.converters.*;
import org.cga.sctp.targeting.importation.parameters.*;
import org.cga.sctp.targeting.importation.validation.IdValidators;
import org.cga.sctp.targeting.importation.validation.LocationValidators;
import org.cga.sctp.utils.LocaleUtils;

import javax.persistence.Convert;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Class used for reading records from the import file (CSV)
 */
@Entity
@Table(name = "ubr_csv_imports")
@SuppressWarnings("unused")
public class UbrHouseholdImport extends CommonHouseholdAttributes {

    /**
     * Do not re-arrange
     */
    public enum ValidationStatus {
        Valid,
        Error
    }

    public UbrHouseholdImport(){
        archived = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long dataImportId;
    private LocalDateTime createdAt;

    private boolean archived;

    @Enumerated(value = EnumType.STRING)
    private ValidationStatus validationStatus;

    @Convert(converter = ListAttributeConverter.class)
    private List<String> errors;

    @Parsed(field = "household_id")
    protected Long householdId;

    @com.univocity.parsers.annotations.Convert(conversionClass = IdValidators.ForNumberConverter.class)
    @Parsed(field = "form_number", applyDefaultConversion = false)
    @Validate
    protected Long formNumber;

    @Column(name = "household_ml_code")
    protected String householdCode;

    @Parsed(field = "pmt_score")
    private BigDecimal pmtScore;

    @Parsed(field = "wealth_quintile")
    @Convert(converter = WealthQuintileParameterValueConverter.class)
    @EnumOptions(selectors = {EnumSelector.CUSTOM_FIELD}, customElement = "title")
    private WealthQuintile wealthQuintile;

    @Parsed(field = "roof_type")
    @Convert(converter = RoofTypeParameterValueConverter.class)
    @EnumOptions(selectors = EnumSelector.CUSTOM_FIELD, customElement = "code")
    private RoofType roofType;

    @Validate
    @Parsed(field = "registration_date", applyDefaultConversion = false)
    @com.univocity.parsers.annotations.Convert(conversionClass = DateConversion.class, args = "MM/dd/yyyy")
    protected Date registrationDate;

    @Parsed(field = "marital_status_code")
    @Validate
    @EnumOptions(selectors = EnumSelector.CUSTOM_FIELD, customElement = "code")
    @Convert(converter = MaritalStatusParameterValueConverter.class)
    private MaritalStatus maritalStatus;

    @Parsed(field = "disability_code")
    @Validate
    @EnumOptions(selectors = EnumSelector.CUSTOM_FIELD, customElement = "code")
    @Convert(converter = DisabilityParameterValueConverter.class)
    private Disability disability;

    @Parsed(field = "chronic_illness_code")
    @Validate
    @EnumOptions(selectors = EnumSelector.CUSTOM_FIELD, customElement = "code")
    @Convert(converter = ChronicIllnessParameterValueConverter.class)
    private ChronicIllness chronicIllness;

    @Parsed(field = "orphan_status_code", defaultNullRead = "NotOrphaned")
    @Validate
    @EnumOptions(selectors = EnumSelector.CUSTOM_FIELD, customElement = "code")
    @Convert(converter = OrphanStatusParameterValueConverter.class)
    private Orphanhood orphanStatus;

    @Validate
    @Parsed(field = "district_code")
    protected Integer districtCode;

    @Validate
    @Parsed(field = "traditional_authority_code")
    protected Integer taCode;

    private Long batchNumber;

    @Validate
    @Parsed(field = "cluster_code")
    protected Integer clusterCode;

    @Validate
    @Parsed(field = "zone_code")
    protected Integer zoneCode;

    @Validate
    @Parsed(field = "village_code")
    protected Integer villageCode;

    @Parsed(field = "village_name")
    protected String villageName;



    @Validate(validators = LocationValidators.LatitudeValidator.class)
    @Parsed(field = "gps_latitude")
    protected BigDecimal gpsLatitude;

    @Validate(validators = LocationValidators.LongitudeValidator.class)
    @Parsed(field = "gps_longitude")
    protected BigDecimal gpsLongitude;

    @Parsed(field = "household_head_name")
    private String householdHeadName;

    @Parsed(field = "district_name")
    private String districtName;

    @Parsed(field = "traditional_authority_name")
    private String traditionalAuthorityName;

    @Parsed(field = "group_village_head_name")
    private String groupVillageHeadName;

    @Parsed(field = "group_village_head_code")
    private Long groupVillageHeadCode;

    @Parsed(field = "cluster_name")
    private String clusterName;

    @Parsed(field = "zone_name")
    private String zoneName;

    @Validate
    @Trim
    @Parsed(field = "first_name")
    protected String firstName;

    @Validate
    @Trim
    @Parsed(field = "last_name")
    protected String lastName;

    @Validate
    @Parsed(field = "gender")
    @Convert(converter = GenderParameterValueConverter.class)
    protected Gender gender;

    @Parsed(field = "highest_education_level")
    @Validate
    @EnumOptions
    @Convert(converter = EducationLevelParameterValueConverter.class)
    protected EducationLevel highestEducationLevel;

    @Validate
    @BooleanString(trueStrings = "YES", falseStrings = "NO")
    @Parsed(field = "fit_for_work")
    protected boolean fitForWork;

    @Parsed(field = "sct_member_code")
    @NullString(nulls = {"-", ""})
    @Validate(nullable = true)
    protected String sctMemberCode;

    @Validate
    @Parsed(field = "date_of_birth")
    @com.univocity.parsers.annotations.Convert(conversionClass = DateConversion.class, args = "MM/dd/yyyy")
    protected Date dateOfBirth;

    @Parsed(field = "relationship_code")
    @Validate
    @EnumOptions(customElement = "code", selectors = {EnumSelector.CUSTOM_FIELD})
    @Convert(converter = RelationshipToHeadParameterValueConverter.class)
    protected RelationshipToHead relationshipToHead;

    @Parsed(field = "national_id")
    @Trim
    @NullString(nulls = {"-", ""})
    @Validate(nullable = true, matches = "^[A-Z0-9]{8}$")
    protected String nationalId;

    @Validate
    @Parsed(field = "household_member_id")
    protected Long householdMemberId;

    @Parsed(field = "member_mobile_number")
    @Trim
    @NullString(nulls = {"", "-"})
    @Validate(nullable = true, allowBlanks = true, matches = "^0?[89][0-9]{8}$")
    protected String memberMobileNumber;

    @Validate
    @Parsed(field = "house_ownership")
    @EnumOptions/*(customElement = "code", selectors = {EnumSelector.CUSTOM_FIELD})*/
    @Convert(converter = HouseOwnershipParameterValueConverter.class)
    protected HouseOwnership houseOwnership;

    @Parsed(field = "house_type")
    @Validate
    @EnumOptions
    @Convert(converter = HouseTypeParameterValueConverter.class)
    protected HouseType houseType;

    @Validate
    @Parsed(field = "house_condition")
    @EnumOptions/*(customElement = "code", selectors = {EnumSelector.CUSTOM_FIELD})*/
    @Convert(converter = HouseConditionParameterValueConverter.class)
    protected HouseCondition houseCondition;

    @Parsed(field = "wall_type")
    @Validate
    @EnumOptions
    @Convert(converter = WallTypeParameterValueConverter.class)
    protected WallType wallType;

    @Parsed(field = "floor_type")
    @Validate
    @EnumOptions/*(customElement = "code", selectors = {EnumSelector.CUSTOM_FIELD})*/
    @Convert(converter = FloorTypeParameterValueConverter.class)
    protected FloorType floorType;

    @Parsed(field = "latrine_type")
    @Validate
    @EnumOptions
    @Convert(converter = LatrineTypeParameterValueConverter.class)
    protected LatrineType latrineType;

    @Parsed(field = "fuel_source")
    @Validate
    @EnumOptions/*(customElement = "code", selectors = {EnumSelector.CUSTOM_FIELD})*/
    @Convert(converter = FuelSourceParameterValueConverter.class)
    protected FuelSource fuelSource;

    @Parsed(field = "last_harvest")
    @Validate
    @Convert(converter = HarvestedProduceDurationParameterValueConverter.class)
    protected HarvestedProduceDuration lastHarvest;

    @Parsed(field = "current_harvest")
    @Validate
    @Convert(converter = HarvestedProduceDurationParameterValueConverter.class)
    protected HarvestedProduceDuration currentHarvest;

    @Parsed(field = "meals")
    @Validate
    @EnumOptions
    @Convert(converter = MealsPerDayParameterValueConverter.class)
    protected MealsPerDay meals;

    @Parsed(field = "assistance_received")
    @Validate
    @BooleanString(trueStrings = "YES", falseStrings = "NO")
    protected boolean assistanceReceived;

    @Transient
    @Parsed(field = "assets")
    @com.univocity.parsers.annotations.Convert(conversionClass = CollectionConversion.class, args = "Set")
    private Set<String> assets;

    @Transient
    @Parsed(field = "livelihood_sources")
    @com.univocity.parsers.annotations.Convert(conversionClass = CollectionConversion.class, args = "Set")
    private Set<String> livelihoodSources;

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Orphanhood getOrphanStatus() {
        return orphanStatus;
    }

    public void setOrphanStatus(Orphanhood orphanStatus) {
        this.orphanStatus = orphanStatus;
    }

    public ChronicIllness getChronicIllness() {
        return chronicIllness;
    }

    public void setChronicIllness(ChronicIllness chronicIllness) {
        this.chronicIllness = chronicIllness;
    }

    public Disability getDisability() {
        return disability;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
    }

    public Long getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Long batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDataImportId() {
        return dataImportId;
    }

    public void setDataImportId(Long dataImportId) {
        this.dataImportId = dataImportId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public Long getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(Long formNumber) {
        this.formNumber = formNumber;
    }

    public String getHouseholdCode() {
        return householdCode;
    }

    public void setHouseholdCode(String householdCode) {
        this.householdCode = householdCode;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    public Integer getTaCode() {
        return taCode;
    }

    public void setTaCode(Integer taCode) {
        this.taCode = taCode;
    }

    public Integer getClusterCode() {
        return clusterCode;
    }

    public void setClusterCode(Integer clusterCode) {
        this.clusterCode = clusterCode;
    }

    public Integer getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(Integer zoneCode) {
        this.zoneCode = zoneCode;
    }

    public Integer getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(Integer villageCode) {
        this.villageCode = villageCode;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public BigDecimal getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(BigDecimal gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public BigDecimal getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(BigDecimal gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public EducationLevel getHighestEducationLevel() {
        return highestEducationLevel;
    }

    public void setHighestEducationLevel(EducationLevel highestEducationLevel) {
        this.highestEducationLevel = highestEducationLevel;
    }

    public boolean isFitForWork() {
        return fitForWork;
    }

    public void setFitForWork(boolean fitForWork) {
        this.fitForWork = fitForWork;
    }

    public String getSctMemberCode() {
        return sctMemberCode;
    }

    public void setSctMemberCode(String sctMemberCode) {
        this.sctMemberCode = sctMemberCode;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public RelationshipToHead getRelationshipToHead() {
        return relationshipToHead;
    }

    public void setRelationshipToHead(RelationshipToHead relationshipToHead) {
        this.relationshipToHead = relationshipToHead;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getMemberMobileNumber() {
        return memberMobileNumber;
    }

    public void setMemberMobileNumber(String memberMobileNumber) {
        if (!LocaleUtils.isStringNullOrEmpty(memberMobileNumber)) {
            if (!memberMobileNumber.startsWith("0")) {
                memberMobileNumber = "0" + memberMobileNumber;
            }
        }
        this.memberMobileNumber = memberMobileNumber;
    }

    public HouseOwnership getHouseOwnership() {
        return houseOwnership;
    }

    public void setHouseOwnership(HouseOwnership houseOwnership) {
        this.houseOwnership = houseOwnership;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    public HouseCondition getHouseCondition() {
        return houseCondition;
    }

    public void setHouseCondition(HouseCondition houseCondition) {
        this.houseCondition = houseCondition;
    }

    public WallType getWallType() {
        return wallType;
    }

    public void setWallType(WallType wallType) {
        this.wallType = wallType;
    }

    public FloorType getFloorType() {
        return floorType;
    }

    public void setFloorType(FloorType floorType) {
        this.floorType = floorType;
    }

    public LatrineType getLatrineType() {
        return latrineType;
    }

    public void setLatrineType(LatrineType latrineType) {
        this.latrineType = latrineType;
    }

    public FuelSource getFuelSource() {
        return fuelSource;
    }

    public void setFuelSource(FuelSource fuelSource) {
        this.fuelSource = fuelSource;
    }

    public HarvestedProduceDuration getLastHarvest() {
        return lastHarvest;
    }

    public void setLastHarvest(HarvestedProduceDuration lastHarvest) {
        this.lastHarvest = lastHarvest;
    }

    public HarvestedProduceDuration getCurrentHarvest() {
        return currentHarvest;
    }

    public void setCurrentHarvest(HarvestedProduceDuration currentHarvest) {
        this.currentHarvest = currentHarvest;
    }

    public MealsPerDay getMeals() {
        return meals;
    }

    public void setMeals(MealsPerDay meals) {
        this.meals = meals;
    }

    public boolean isAssistanceReceived() {
        return assistanceReceived;
    }

    public void setAssistanceReceived(boolean assistanceReceived) {
        this.assistanceReceived = assistanceReceived;
    }

    public Long getHouseholdMemberId() {
        return householdMemberId;
    }

    public void setHouseholdMemberId(Long householdMemberId) {
        this.householdMemberId = householdMemberId;
    }

    public Set<String> getAssets() {
        return assets;
    }

    public void setAssets(Set<String> assets) {
        this.assets = assets;
    }

    public Set<String> getLivelihoodSources() {
        return livelihoodSources;
    }

    public void setLivelihoodSources(Set<String> livelihoodSources) {
        this.livelihoodSources = livelihoodSources;
    }

    public BigDecimal getPmtScore() {
        return pmtScore;
    }

    public void setPmtScore(BigDecimal pmtScore) {
        this.pmtScore = pmtScore;
    }

    public WealthQuintile getWealthQuintile() {
        return wealthQuintile;
    }

    public void setWealthQuintile(WealthQuintile wealthQuintile) {
        this.wealthQuintile = wealthQuintile;
    }

    public String getHouseholdHeadName() {
        return householdHeadName;
    }

    public void setHouseholdHeadName(String householdHeadName) {
        this.householdHeadName = householdHeadName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getTraditionalAuthorityName() {
        return traditionalAuthorityName;
    }

    public void setTraditionalAuthorityName(String traditionalAuthorityName) {
        this.traditionalAuthorityName = traditionalAuthorityName;
    }

    public String getGroupVillageHeadName() {
        return groupVillageHeadName;
    }

    public void setGroupVillageHeadName(String groupVillageHeadName) {
        this.groupVillageHeadName = groupVillageHeadName;
    }

    public Long getGroupVillageHeadCode() {
        return groupVillageHeadCode;
    }

    public void setGroupVillageHeadCode(Long groupVillageHeadCode) {
        this.groupVillageHeadCode = groupVillageHeadCode;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public RoofType getRoofType() {
        return roofType;
    }

    public void setRoofType(RoofType roofType) {
        this.roofType = roofType;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @Override
    public String toString() {
        return "UbrHouseholdImportRecord{"
                + "name=" + firstName + " " + lastName
                + ",UBR-ID=" + formNumber
                + ",HH_ID=" + householdId
                + "}";
    }
}
