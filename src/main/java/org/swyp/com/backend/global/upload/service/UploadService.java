package org.swyp.com.backend.global.upload.service;

import org.swyp.com.backend.global.upload.dto.PresignedUploadResponse;

public interface UploadService {

    PresignedUploadResponse createPresignedUploadUrl(String originalFilename, String contentType);
}
