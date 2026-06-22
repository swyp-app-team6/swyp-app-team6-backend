package org.swyp.com.backend.global.upload.dto;

public record PresignedUploadResponse(
        String uploadUrl,
        String key
) {

}
