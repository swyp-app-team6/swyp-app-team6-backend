package org.swyp.com.backend.image.service;

public interface ImageUrlService {

    /**
     * S3 object key를 CloudFront Signed URL로 변환한다. imageKey가 null이면 null을 반환한다.
     */
    String toSignedUrl(String imageKey);

    /**
     * S3 object key의 main 압축본 경로를 thumbnail 압축본 경로로 바꿔 CloudFront Signed URL을 발급한다.
     * imageKey가 null이면 null을 반환하고, main 접두사로 시작하지 않는 key(레거시 등)는 원본 그대로 서명한다.
     */
    String toSignedThumbnailUrl(String imageKey);
}
