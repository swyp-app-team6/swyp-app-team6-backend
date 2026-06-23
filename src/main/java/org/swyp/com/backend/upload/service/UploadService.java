package org.swyp.com.backend.upload.service;

import org.swyp.com.backend.upload.dto.PresignedUploadResponse;

public interface UploadService {

    PresignedUploadResponse createPresignedUploadUrl(String originalFilename, String contentType);
}
