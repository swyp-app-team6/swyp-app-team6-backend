package org.swyp.com.backend.image.service;

public interface ImageUrlService {

    /**
     * key를 그대로 CloudFront Signed URL로 변환한다. cosmic 아이콘처럼 이미 완전한 경로를 가진 key에 사용한다.
     * key가 null이면 null을 반환한다.
     */
    String toSignedUrl(String key);

    /**
     * 사용자 업로드 이미지의 bare id(prefix 없는 id-uuid)를 main 압축본 경로로 변환해 서명한다.
     * imageKey가 null이면 null을 반환한다.
     */
    String toSignedMainUrl(String imageKey);

    /**
     * 사용자 업로드 이미지의 bare id(prefix 없는 id-uuid)를 thumbnail 압축본 경로로 변환해 서명한다.
     * imageKey가 null이면 null을 반환한다.
     */
    String toSignedThumbnailUrl(String imageKey);
}
