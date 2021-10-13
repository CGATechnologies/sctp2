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

import org.cga.sctp.targeting.CbtStatus;
import org.cga.sctp.targeting.importation.converters.*;
import org.cga.sctp.targeting.importation.parameters.*;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Entity
@Table(name = "households")
public class Household extends CommonHouseholdAttributes {
    @Id
    private Long householdId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
    private String locationCode;
    private Integer taCode;
    private Integer clusterCode;
    private Integer zoneCode;
    private Integer villageCode;
    private String villageName;
    private Long groupVillageHeadCode;
    private String groupVillageHeadName;
    private String mlCode;
    private String ubrCode;
    private String accountNumber;
    private Long ubrCsvBatchNumber;

    private Integer cbtRank;
    private Integer generalRank;
    private LocalDateTime lastCbtRanking;
    private LocalDateTime lastGeneralRanking;

    private Boolean cbtSelection;
    private Long cbtSessionId;
    private BigDecimal cbtPmt;

    @Convert(converter = CbtStatus.Converter.class)
    private CbtStatus cbtStatus;



    @Convert(converter = FloorTypeParameterValueConverter.class)
    private FloorType floorType;

    @Convert(converter = RoofTypeParameterValueConverter.class)
    private RoofType roofType;

    @Convert(converter = WallTypeParameterValueConverter.class)
    private WallType wallType;

    @Convert(converter = LatrineTypeParameterValueConverter.class)
    private LatrineType latrineType;

    @Convert(converter = HouseConditionParameterValueConverter.class)
    private HouseCondition houseCondition;

    @Convert(converter = FuelSourceParameterValueConverter.class)
    private FuelSource fuelSource;

    @Convert(converter = HarvestedProduceDurationParameterValueConverter.class)
    private HarvestedProduceDuration maizeHarvestLasted;

    @Convert(converter = HarvestedProduceDurationParameterValueConverter.class)
    private HarvestedProduceDuration maizeInGranaryWillLast;

    @Convert(converter = MealsPerDayParameterValueConverter.class)
    private MealsPerDay mealsEatenLastWeek;

    private Boolean receivesMonetaryAssistance;
    /*private Boolean signedTargetingForm;
    private Boolean thumbprintOnTargetingForm;
    private LocalDate dateCollected;*/

    public Long getUbrCsvBatchNumber() {
        return ubrCsvBatchNumber;
    }

    public void setUbrCsvBatchNumber(Long ubrCsvBatchNumber) {
        this.ubrCsvBatchNumber = ubrCsvBatchNumber;
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

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getMlCode() {
        return mlCode;
    }

    public void setMlCode(String mlCode) {
        this.mlCode = mlCode;
    }

    public String getUbrCode() {
        return ubrCode;
    }

    public void setUbrCode(String ubrCode) {
        this.ubrCode = ubrCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public FloorType getFloorType() {
        return floorType;
    }

    public void setFloorType(FloorType floorType) {
        this.floorType = floorType;
    }

    public RoofType getRoofType() {
        return roofType;
    }

    public void setRoofType(RoofType roofType) {
        this.roofType = roofType;
    }

    public WallType getWallType() {
        return wallType;
    }

    public void setWallType(WallType wallType) {
        this.wallType = wallType;
    }

    public LatrineType getLatrineType() {
        return latrineType;
    }

    public void setLatrineType(LatrineType latrineType) {
        this.latrineType = latrineType;
    }

    public HouseCondition getHouseCondition() {
        return houseCondition;
    }

    public void setHouseCondition(HouseCondition houseCondition) {
        this.houseCondition = houseCondition;
    }

    public FuelSource getFuelSource() {
        return fuelSource;
    }

    public void setFuelSource(FuelSource fuelSource) {
        this.fuelSource = fuelSource;
    }

    public HarvestedProduceDuration getMaizeHarvestLasted() {
        return maizeHarvestLasted;
    }

    public void setMaizeHarvestLasted(HarvestedProduceDuration maizeHarvestLasted) {
        this.maizeHarvestLasted = maizeHarvestLasted;
    }

    public HarvestedProduceDuration getMaizeInGranaryWillLast() {
        return maizeInGranaryWillLast;
    }

    public void setMaizeInGranaryWillLast(HarvestedProduceDuration maizeInGranaryWillLast) {
        this.maizeInGranaryWillLast = maizeInGranaryWillLast;
    }

    public MealsPerDay getMealsEatenLastWeek() {
        return mealsEatenLastWeek;
    }

    public void setMealsEatenLastWeek(MealsPerDay mealsEatenLastWeek) {
        this.mealsEatenLastWeek = mealsEatenLastWeek;
    }

    public Boolean getReceivesMonetaryAssistance() {
        return receivesMonetaryAssistance;
    }

    public void setReceivesMonetaryAssistance(Boolean receivesMonetaryAssistance) {
        this.receivesMonetaryAssistance = receivesMonetaryAssistance;
    }

    /*public Boolean getSignedTargetingForm() {
        return signedTargetingForm;
    }

    public void setSignedTargetingForm(Boolean signedTargetingForm) {
        this.signedTargetingForm = signedTargetingForm;
    }

    public Boolean getThumbprintOnTargetingForm() {
        return thumbprintOnTargetingForm;
    }

    public void setThumbprintOnTargetingForm(Boolean thumbprintOnTargetingForm) {
        this.thumbprintOnTargetingForm = thumbprintOnTargetingForm;
    }

    public LocalDate getDateCollected() {
        return dateCollected;
    }

    public void setDateCollected(LocalDate dateCollected) {
        this.dateCollected = dateCollected;
    }
*/
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

    public Long getGroupVillageHeadCode() {
        return groupVillageHeadCode;
    }

    public void setGroupVillageHeadCode(Long groupVillageHeadCode) {
        this.groupVillageHeadCode = groupVillageHeadCode;
    }

    public String getGroupVillageHeadName() {
        return groupVillageHeadName;
    }

    public void setGroupVillageHeadName(String groupVillageHeadName) {
        this.groupVillageHeadName = groupVillageHeadName;
    }

    public Integer getCbtRank() {
        return cbtRank;
    }

    public void setCbtRank(Integer cbtRank) {
        this.cbtRank = cbtRank;
    }

    public Integer getGeneralRank() {
        return generalRank;
    }

    public void setGeneralRank(Integer generalRank) {
        this.generalRank = generalRank;
    }

    public LocalDateTime getLastCbtRanking() {
        return lastCbtRanking;
    }

    public void setLastCbtRanking(LocalDateTime lastCbtRanking) {
        this.lastCbtRanking = lastCbtRanking;
    }

    public LocalDateTime getLastGeneralRanking() {
        return lastGeneralRanking;
    }

    public void setLastGeneralRanking(LocalDateTime lastGeneralRanking) {
        this.lastGeneralRanking = lastGeneralRanking;
    }

    public Long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(Long householdId) {
        this.householdId = householdId;
    }

    public Boolean getCbtSelection() {
        return cbtSelection;
    }

    public void setCbtSelection(Boolean cbtSelection) {
        this.cbtSelection = cbtSelection;
    }

    public Long getCbtSessionId() {
        return cbtSessionId;
    }

    public void setCbtSessionId(Long cbtSessionId) {
        this.cbtSessionId = cbtSessionId;
    }

    public BigDecimal getCbtPmt() {
        return cbtPmt;
    }

    public void setCbtPmt(BigDecimal cbtPmt) {
        this.cbtPmt = cbtPmt;
    }

    public CbtStatus getCbtStatus() {
        return cbtStatus;
    }

    public void setCbtStatus(CbtStatus cbtStatus) {
        this.cbtStatus = cbtStatus;
    }
}
