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

package org.cga.sctp.data;

import lib.gintec_rdl.spector.Spector;
import lib.gintec_rdl.spector.TypeInfo;
import org.cga.sctp.core.TransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ResourceService extends TransactionalService {
    @Autowired
    private DatastoreConfiguration configuration;

    public static class FileInspectionResult {
        private final long cacheKey;
        private TypeInfo typeInfo;

        public FileInspectionResult(long cacheKey, TypeInfo typeInfo) {
            this.cacheKey = cacheKey;
            this.typeInfo = typeInfo;
        }

        public boolean isAcceptedFile() {
            return typeInfo != null;
        }

        public TypeInfo getTypeInfo() {
            return typeInfo;
        }
    }

    public TypeInfo getFileType(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        return getFileContentType(file).typeInfo;
    }

    public FileInspectionResult inspectFile(MultipartFile file, @Nullable FileInspectionResult cache) {
        return getFileContentType(file, cache);
    }

    public boolean isAcceptedImageFile(MultipartFile file) {
        return getFileType(file) != null;
    }

    private FileInspectionResult getFileContentType(MultipartFile file) {
        return getFileContentType(file, null);
    }

    /**
     * <p>This is an expensive</p>
     *
     * @param file   Uploaded file
     * @param result Result containing cached file information.
     *               The file cached file is used for inspection and will be recreated if this
     *               is null or the cache key does not resolve to a previously cached file.
     * @return File inspection result, containing the file type information and cache key of the
     * inspection file (copy of the uploaded file).<br />
     * If calling this method multiple times, pass the file inspection result from the initial call.
     */
    private FileInspectionResult getFileContentType(MultipartFile file, @Nullable FileInspectionResult result) {
        if (result == null) {
            result = new FileInspectionResult(System.currentTimeMillis(), null);
        }
        final File targetFile = Path.of(System.getProperty("java.io.tmpdir"))
                .toAbsolutePath()
                .normalize()
                .toAbsolutePath()
                .resolve(format("upload_%d.tmp", result.cacheKey))
                .toFile();
        try {
            if (!targetFile.exists()) {
                LOG.info("Creating temp file for inspection {}", targetFile);
                file.transferTo(targetFile);
            } else {
                LOG.info("Temp file for inspection already exists: {}", targetFile);
            }
            result.typeInfo = Spector.inspect(targetFile);
        } catch (Exception e) {
            LOG.error("Failed to get uploaded file's content type.", e);
            result.typeInfo = null;
        }
        return result;
    }

    public boolean moveUploadedFile(MultipartFile file, File dstDir, String fileName) {
        try {
            file.transferTo(new File(dstDir, fileName).getAbsoluteFile());
            return true;
        } catch (IOException e) {
            LOG.error("Failure moving uploaded file to destination.", e);
        }
        return false;
    }

    private boolean isInUploadDirectory(File parent, File file) {
        Path child = file.getAbsoluteFile().toPath().normalize();
        Path parentPath = parent.getAbsoluteFile().toPath().normalize();

        return child.getNameCount() > parentPath.getNameCount() // prevents marking the parent as a child of itself (grave consequence)
                && child.startsWith(parentPath);
    }

    /**
     * Delete given file .
     *
     * @param file file to delete
     * @return true if deleted.
     */
    public boolean deleteFile(File file) {
        try {
            return Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            LOG.error("Failure deleting file", e);
            return false;
        }
    }

    private String createRecipientPhotoName(long household, boolean main) {
        return format("%x_%s", household, main ? "main" : "alt");
    }

    private UpdateResult storeRecipientPhoto(MultipartFile photo, long household, boolean main) {
        try {
            // we don't use file extension so that it replaces the same picture
            String name, type;
            name = createRecipientPhotoName(household, main);
            type = getFileType(photo).getMime();
            photo.transferTo(new File(configuration.getRecipientPhotoDirectory(), name).getAbsoluteFile());
            return new UpdateResult(true, name, type);
        } catch (Exception e) {
            LOG.error("Failed to store recipient photo for household {} "+e, household);
            return new UpdateResult(false, null, null, "error processing file");
        }
    }

    private UpdateResult storeRecipientPhoto(UploadFileValidator.UploadedFile photo, long household, boolean main) {
        try {
            // we don't use file extension so that it replaces the same picture
            String name = createRecipientPhotoName(household, main);
            photo.transferTo(new File(configuration.getRecipientPhotoDirectory(), name).getAbsoluteFile());
            return new UpdateResult(true, name, photo.getTypeInfo().getMime());
        } catch (Exception e) {
            LOG.error("Failed to store recipient photo for household {}", household);
            return new UpdateResult(false, null, null, "error processing file");
        }
    }

    public Resource getRecipientPhotoResource(Long household, boolean main) {
        File photo = new File(configuration.getRecipientPhotoDirectory(), createRecipientPhotoName(household, main));
        return photo.exists() ? getFileAsResource(photo) : null;
    }

    public record UpdateResult(boolean stored, String name, String type, String error) {
        public UpdateResult(boolean stored, String name, String type) {
            this(stored, name, type, null);
        }
    }

    public UpdateResult storeMainRecipientPhoto(MultipartFile photo, long household) {
        return storeRecipientPhoto(photo, household, true);
    }

    public UpdateResult storeMainRecipientPhoto(UploadFileValidator.UploadedFile photo, long household) {
        return storeRecipientPhoto(photo, household, true);
    }

    public UpdateResult storeAlternateRecipientPhoto(UploadFileValidator.UploadedFile photo, long household) {
        return storeRecipientPhoto(photo, household, false);
    }

    public UpdateResult storeAlternateRecipientPhoto(MultipartFile photo, long household) {
        return storeRecipientPhoto(photo, household, false);
    }

    /**
     * @param file .
     * @return .
     * @throws SecurityException If file is not is the upload directory
     */
    public Resource getFileAsResource(File file) throws SecurityException {
        return new FileSystemResource(file);
    }
}
