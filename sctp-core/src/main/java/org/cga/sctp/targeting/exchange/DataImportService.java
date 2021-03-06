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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.importation.ImportTaskService;
import org.cga.sctp.targeting.importation.UbrHouseholdImport;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataImportService extends TransactionalService {

    @Autowired
    private DataImportsViewRepository viewRepository;

    @Autowired
    private DataImportRepository importRepository;

    public List<DataImportView> getDataImportsByImporter(Long userId) {
        return viewRepository.findByImporterUserId(userId);
    }

    public Page<DataImportView> getDataImports(Pageable pageable) {
        return viewRepository.findAllOrderByIdDesc(pageable);
    }

    public void saveDataImport(DataImport dataImport) {
        importRepository.save(dataImport);
    }

    public DataImport findImportByIdAndStatus(Long id, DataImportObject.ImportSource importSource, DataImportObject.ImportStatus importStatus) {
        return importRepository.findImportByIdAndDataSourceAndStatus(id, importSource, importStatus);
    }

    public DataImport findPendingUbrCsvImport(Long id) {
        return importRepository.findImportByIdAndDataSourceAndStatus(
                id,
                DataImportObject.ImportSource.UBR_CSV,
                DataImportObject.ImportStatus.FileUploadPending
        );
    }

    public DataImport findDataImportById(Long id) {
        return importRepository.findById(id).orElse(null);
    }

    public DataImportView findImportViewByIdAndStatus(Long id, DataImportObject.ImportSource importSource, DataImportObject.ImportStatus importStatus) {
        return viewRepository.findImportByIdAndDataSourceAndStatus(id, importSource, importStatus);
    }

    public void deleteDataImport(DataImport dataImport) {
        importRepository.delete(dataImport);
    }

    public DataImportView findDataImportViewById(Long id) {
        return viewRepository.findById(id).orElse(null);
    }

    @Transactional
    public void closeImportSession(DataImport dataImport) {
        // Ideally, do all the updates in the stored procedure. But this way it's future proof
        importRepository.save(dataImport);
        importRepository.closeDataImportSession(dataImport.getId());
    }

    public void calculateImportSessionDuplicates(DataImportView dataImport) {
        importRepository.closeDataImportSession(dataImport.getId());
    }

    public void mergeBatchIntoPopulation(DataImportView dataImport) {
        importRepository.mergeBatchIntoPopulation(dataImport.getId(), DataImportObject.ImportStatus.Merged.name());
    }

    // TODO: remove importTaskService and directoryToSaveFile from parameters and make them part of this service?
    public Optional<Path> exportDataImportErrors(Long dataImportId, ImportTaskService importTaskService, String directoryToSaveFile) {
        DataImportView importView = this.findDataImportViewById(dataImportId);
        if (importView == null) {
            return Optional.empty();
        }

        Page<UbrHouseholdImport> householdList = importTaskService.getImportsBySessionIdForReview(dataImportId, Pageable.unpaged());
        try {
            Path filePath = exportHouseholdsWithErrors(householdList.toList(), directoryToSaveFile);
            return Optional.of(filePath);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("Failed to export beneficiaries", e);
        }
        return Optional.empty();
    }

    private Path exportHouseholdsWithErrors(List<UbrHouseholdImport> householdList, String directoryToSaveFile) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Path filePath = Files.createTempFile(Paths.get(directoryToSaveFile), "imported-households", ".xlsx");
            FileOutputStream fos = new FileOutputStream(filePath.toFile());
            Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Households"));
            // Create file headers
            Row tmpExcelRow = sheet.createRow(0);
            Cell cell = tmpExcelRow.createCell(0);
            cell.setCellValue("Account Numbers");

            // Headers
            tmpExcelRow = sheet.createRow(1);
            addCell(tmpExcelRow, 0, "District");
            addCell(tmpExcelRow, 1, "TA");
            addCell(tmpExcelRow, 2, "Village Cluster");
            addCell(tmpExcelRow, 3, "HH Code");
            addCell(tmpExcelRow, 4, "HH Head");
            addCell(tmpExcelRow, 5, "Errors");
            // Add other rows
            int currentRow = 2;
            for (UbrHouseholdImport household : householdList) {
                tmpExcelRow = sheet.createRow(currentRow);
                addCell(tmpExcelRow, 0, household.getDistrictName());
                addCell(tmpExcelRow, 1, household.getTraditionalAuthorityName());
                addCell(tmpExcelRow, 2, household.getVillageName());
                addCell(tmpExcelRow, 3, household.getHouseholdCode());
                addCell(tmpExcelRow, 4, household.getHouseholdHeadName());
                addCell(tmpExcelRow, 5, household.getErrors().stream().collect(Collectors.joining(",")));
                currentRow++;
            }

            workbook.write(fos);
            tmpExcelRow = null;
            sheet = null;

            return filePath;
        } catch (IOException exception) {
            throw exception;
        }
    }

    private void addCell(Row row, int index, String data) {
        Cell cell = row.createCell(index);
        cell.setCellValue(data);
    }
}
