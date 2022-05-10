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

package org.cga.sctp.transfers.accounts;

import com.creditdatamw.zerocell.Reader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.beneficiaries.Household;
import org.cga.sctp.transfers.TransferEventHouseholdView;
import org.cga.sctp.transfers.TransferSession;
import org.cga.sctp.transfers.TransfersRepository;
import org.cga.sctp.transfers.periods.TransferPeriod;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles exporting and importing of account numbers for Transfer Beneficiaries.
 * @since 1.4.0
 */
@Service
public class BeneficiaryAccountService {
    /**
     * Directory to use to store temporary files for the exports
     */
    @Value("${imports.staging}")
    private String stagingDirectory;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private TransfersRepository transfersRepository;

    /**
     * Extract data from CSV
     * @param uploadedFilePath path to uploaded file
     * @return transfer account number list
     */
    public TransferAccountNumberList extractAccountNumberFromCSV(Path uploadedFilePath) {
        final TransferAccountNumberList transferAccountNumberList = new TransferAccountNumberList();
        List<AccountNumberImportRow> rows = Reader.of(AccountNumberImportRow.class)
                .skipFirstNRows(1)
                .from(uploadedFilePath.toFile())
                .list();

        transferAccountNumberList.setAccountNumberList(new ArrayList<>());
        rows.forEach(accountRow -> {
            TransferAccountNumberList.HouseholdAccountNumber hh = new TransferAccountNumberList.HouseholdAccountNumber();
            // hh.setTransferId(); //TODO: Get the transfer id
            // TODO: We can use a record here, `record TransferIdAndHouseholdCode(Long transferId, String mlCode, Long householdId)`
            Optional<Household> household = beneficiaryService.findHouseholdByMLCode(accountRow.getHouseholdCode());
            if (household.isEmpty())
                return;
            hh.setHouseholdId(household.get().getHouseholdId());
            hh.setAccountNumber(accountRow.getAccountNumber());
            transferAccountNumberList.getAccountNumberList().add(hh);
        });
        //  TODO: extract account numbers from the CSV file
        return transferAccountNumberList;
    }

    public int assignAccountNumbers(TransferSession session, TransferPeriod period, TransferAccountNumberList transferAccountNumberList) {
        // TODO: validate the session, check if the the period is open and check transfer agency for E-Payments which is the valid reason to assign account numbers
        List<Long> householdsAssigned = new ArrayList<>();
        for (TransferAccountNumberList.HouseholdAccountNumber hh : transferAccountNumberList.getAccountNumberList()) {
            transfersRepository.findByHouseholdId(hh.getHouseholdId())
                    .ifPresent(transfer -> {
                        // TODO: Check if the transfer period matches the period in the request
                        if (transfer.getTransferPeriodId() != period.getId()) {
                            transfer.setAccountNumber(hh.getAccountNumber());
                            transfer.setModifiedAt(LocalDateTime.now());
                            transfersRepository.save(transfer);

                            householdsAssigned.add(hh.getHouseholdId());
                        }
                    });
        }
        LoggerFactory.getLogger(getClass()).info("Assigned {} account numbers", householdsAssigned.size());
        // TODO: return the actual list?
        return householdsAssigned.size();
    }

    public Path exportBeneficiaryListToExcel(List<TransferEventHouseholdView> householdList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Path filePath = Files.createTempFile(Paths.get(stagingDirectory), "accounts", ".xlsx");
            FileOutputStream fos = new FileOutputStream(filePath.toFile());
            Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("Accounts"));
            // Create file headers
            Row tmpExcelRow = sheet.createRow(0);
            Cell cell = tmpExcelRow.createCell(0);
            cell.setCellValue("Account Numbers");

            // Headers
            tmpExcelRow = sheet.createRow(1);
            addCell(tmpExcelRow,0, "District");
            addCell(tmpExcelRow, 1,"TA");
            addCell(tmpExcelRow, 2,"Village Cluster");
            addCell(tmpExcelRow, 3,"HH Code");
            addCell(tmpExcelRow, 4,"Beneficiary Code");
            addCell(tmpExcelRow, 5,"Beneficiary Name");
            addCell(tmpExcelRow, 6,"Transfer Agency");
            addCell(tmpExcelRow, 7,"Account Number");
            addCell(tmpExcelRow, 8,"Sub-Status");
            addCell(tmpExcelRow, 9,"Date Account Assignment");
            // Add other rows
            int currentRow = 2;
            for (TransferEventHouseholdView household : householdList) {
                tmpExcelRow = sheet.createRow(currentRow);
                addCell(tmpExcelRow, 0, household.getDistrictName());
                addCell(tmpExcelRow, 1, household.getTaName());
                // FIXME: must be cluster
                addCell(tmpExcelRow, 2, household.getVillageName());
                addCell(tmpExcelRow, 3, household.getMlCode());
                // FIXME: must be "Beneficiary Code"
                addCell(tmpExcelRow, 4, household.getReceiverName());
                addCell(tmpExcelRow, 5,  household.getReceiverName());
                addCell(tmpExcelRow, 6,  ""); // TODO: household.getTransferAgency());
                addCell(tmpExcelRow, 7, ""); // TODO:  household.getAccountNumber());
                addCell(tmpExcelRow, 8,  ""); // TODO:  name = "Sub-Status")
                addCell(tmpExcelRow, 9,  ""); // NOTE: typically don't have "Date Account Assignment" at this point
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
