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

package org.cga.sctp.targeting.exchange;

import javax.persistence.*;
import java.time.ZonedDateTime;

@MappedSuperclass
public class DataImportObject {

    public enum ImportSource {
        UBR_CSV("UBR (CSV)", "UBR"),
        UBR_API("UBR (API)", "UBR");

        public final String title;
        public final String provider;

        ImportSource(String title, String provider) {
            this.title = title;
            this.provider = provider;
        }
    }

    public enum ImportStatus {
        FileUploadPending("Waiting For File Upload", "Data file upload not complete."),
        Processing("Processing", "Waiting for data analysis process to finish"),
        Review("In Review", "Import data is under review."),
        Error("Error", "Import process encountered an error. Refer to import process log."),
        Merged("Merged", "Data was successfully imported from source into population"),
        Merging("Merging", "Data is currently being imported from temporary table into population set");

        public final String title;
        public final String description;

        ImportStatus(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public static final ImportStatus[] IMPORT_STATUSES = values();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    private String title;
    private Long households;
    private Long individuals;
    @Enumerated(EnumType.STRING)
    private ImportSource dataSource;
    @Enumerated(EnumType.STRING)
    private ImportStatus status;
    private String statusText;
    private String sourceFile;
    private Long importerUserId;
    private Long batchDuplicates;
    private Long populationDuplicates;
    private ZonedDateTime importDate;
    private ZonedDateTime completionDate;
    private ZonedDateTime mergeDate;

    @Column(updatable = false, insertable = false, columnDefinition = "boolean default false")
    private Boolean readyToMerge;
    private Long newHouseholds;
    private Long oldHouseholds;

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getHouseholds() {
        return households;
    }

    public void setHouseholds(Long households) {
        this.households = households;
    }

    public Long getIndividuals() {
        return individuals;
    }

    public void setIndividuals(Long individuals) {
        this.individuals = individuals;
    }

    public ImportSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(ImportSource dataSource) {
        this.dataSource = dataSource;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Long getImporterUserId() {
        return importerUserId;
    }

    public void setImporterUserId(Long importerUserId) {
        this.importerUserId = importerUserId;
    }

    public Long getBatchDuplicates() {
        return batchDuplicates;
    }

    public void setBatchDuplicates(Long batchDuplicates) {
        this.batchDuplicates = batchDuplicates;
    }

    public Long getPopulationDuplicates() {
        return populationDuplicates;
    }

    public void setPopulationDuplicates(Long populationDuplicates) {
        this.populationDuplicates = populationDuplicates;
    }

    public ZonedDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(ZonedDateTime importDate) {
        this.importDate = importDate;
    }

    public ZonedDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(ZonedDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public Long getNewHouseholds() {
        return newHouseholds;
    }

    public void setNewHouseholds(Long newHouseholds) {
        this.newHouseholds = newHouseholds;
    }

    public Long getOldHouseholds() {
        return oldHouseholds;
    }

    public void setOldHouseholds(Long oldHouseholds) {
        this.oldHouseholds = oldHouseholds;
    }

    public ZonedDateTime getMergeDate() {
        return mergeDate;
    }

    public Boolean getReadyToMerge() {
        return readyToMerge;
    }

    public void setMergeDate(ZonedDateTime mergeDate) {
        this.mergeDate = mergeDate;
    }

    @Override
    public String toString() {
        return "{id=" + id + ", status=" + status + ", source=" + dataSource + "}";
    }
}
