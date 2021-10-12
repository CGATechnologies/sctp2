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

import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.targeting.exchange.DataImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImportTaskService extends TransactionalService {

    @Autowired
    private FileImportTaskRepository taskRepository;

    @Autowired
    private FileImportTaskViewRepository viewRepository;

    @Autowired
    private UbrHouseholdImportRepository householdImportRepository;

    public void saveImportTask(FileImportTask importTask) {
        taskRepository.save(importTask);
    }

    public List<FileImportTaskView> getQueuedImportTaskViews() {
        return viewRepository.findByStatusOrderByIdAsc(FileImportTask.ImportTaskStatus.Queued);
    }

    private FileImportTaskView wrap(FileImportTask task, DataImport dataImport) {
        return new FileImportTaskViewWrapper(dataImport.getSourceFile(), task);
    }

    public FileImportTaskView createTaskFromDataImport(DataImport dataImport) {
        FileImportTask task = new FileImportTask();
        task.setCreatedAt(LocalDateTime.now());
        task.setDataImportId(dataImport.getId());
        task.setStatus(BaseFileImportTask.ImportTaskStatus.Queued);
        task.setCurrentRow(0L);
        task.setRowCount(0L);
        saveImportTask(task);
        return wrap(task, dataImport);
    }

    public void updateTaskWithView(FileImportTaskView view) {
        if (view instanceof FileImportTaskViewWrapper wrapper) {
            saveImportTask(wrapper.getDelegate());
        } else {
            taskRepository.updateTask(view.getId(), view.getStatus().name(),
                    view.getRowCount(), view.getCurrentRow(), view.getFinishedAt());
        }
    }

    public void saveImports(List<UbrHouseholdImport> imports) {
        householdImportRepository.saveAll(imports);
    }

    public void deleteFileImportTask(FileImportTaskView task) {
        if (task instanceof FileImportTaskViewWrapper wrapper) {
            taskRepository.delete(wrapper.getDelegate());
        } else {
            taskRepository.deleteById(task.getId());
        }
    }

    public List<UbrHouseholdImport> getImportsBySessionIdForReview(Long dataImportId, Pageable pageable) {
        return householdImportRepository.findByDataImportIdAndArchived(dataImportId, false, pageable);
    }

    public void deleteHouseholdImport(UbrHouseholdImport householdImport) {
        householdImportRepository.delete(householdImport);
    }

    public UbrHouseholdImport getHouseholdImportByIdAndDataImportId(Long id, Long dataImportId) {
        return householdImportRepository.findByIdAndDataImportId(id, dataImportId);
    }

    public List<UbrHouseholdImport> getDataImportDuplicates(Long id, Pageable pageable) {
        return householdImportRepository.getDataImportDuplicates(id, pageable.getPageNumber(), pageable.getPageSize());
    }

    public void saveHouseholdImport(UbrHouseholdImport householdImport) {
        householdImportRepository.save(householdImport);
    }
}
