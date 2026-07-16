package org.swyp.com.backend.image.service;

public interface ImageUrlService {

    /**
     * S3 object key를 CloudFront Signed URL로 변환한다. imageKey가 null이면 null을 반환한다.
     */
    String toSignedUrl(String imageKey);
}
