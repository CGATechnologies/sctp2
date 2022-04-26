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

package org.cga.sctp.mis.file_upload;

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.upload.UploadInfo;
import org.cga.sctp.core.TransactionalService;
import org.cga.sctp.mis.targeting.import_tasks.FileImportConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileUploadService extends TransactionalService {

    @Autowired
    private FileUploadWebConfig config;

    @Autowired
    private FileImportConfig importConfig;

    @Autowired
    private TusFileUploadService tusFileUploadService;

    public boolean moveToStagingDirectory(InputStream inputStream, UploadInfo uploadInfo) {
        try {
            Path output = this.importConfig.getStagingDirectory().toPath().resolve(uploadInfo.getId().toString());
            Files.copy(inputStream, output, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            LOG.error("Failure moving uploaded file to staging directory", e);
            return false;
        }
    }

    public Optional<Path> moveToStagingDirectoryAndGetPath(InputStream inputStream, UploadInfo uploadInfo) {
        try {
            Path output = this.importConfig.getStagingDirectory().toPath().resolve(uploadInfo.getId().toString());
            Files.copy(inputStream, output, StandardCopyOption.REPLACE_EXISTING);
            return Optional.of(output);
        } catch (IOException e) {
            LOG.error("Failure moving uploaded file to staging directory", e);
            return Optional.empty();
        }
    }

    /**
     * Every midnight, cleanup unused files.
     */
    @Scheduled(cron = "@midnight")
    void removeStaleUploads() {
        Path locksDir = new File(config.getTmpUploadDir()).toPath().toAbsolutePath().resolve("locks");
        if (Files.exists(locksDir)) {
            try {
                tusFileUploadService.cleanup();
            } catch (IOException e) {
                LOG.error("Error when attempting to cleanup files", e);
            }
        }
    }
}
