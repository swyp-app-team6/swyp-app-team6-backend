package org.swyp.com.backend.upload.dto;

public record PresignedUploadResponse(
        String uploadUrl,
        String imageKey
) {

}
