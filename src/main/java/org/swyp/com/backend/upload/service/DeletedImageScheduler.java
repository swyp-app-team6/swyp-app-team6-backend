package org.swyp.com.backend.upload.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.swyp.com.backend.upload.domain.DeletedImage;
import org.swyp.com.backend.upload.domain.repository.DeletedImageRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedImageScheduler {
    private final S3Client s3Client;
    private final DeletedImageRepository deletedImageRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.bucket.package.original}")
    private String originalPackagePrefix;
    @Value("${aws.s3.bucket.package.main}")
    private String mainPackagePrefix;
    @Value("${aws.s3.bucket.package.thumbnail}")
    private String thumbnailPackagePrefix;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteImages() {
        List<DeletedImage> deletedImageList = deletedImageRepository.findAllByDeletedFalse();

        for (DeletedImage deletedImage : deletedImageList) {
            boolean res = delete(deletedImage.getImageKey());

            if (res) {
                deletedImage.completeDelete();
                deletedImageRepository.save(deletedImage);
            }
        }
    }

    /**
     * 이미지 하나는 original(원본)/main/thumbnail 세 개의 물리 파일로 존재하므로 셋 다 지운다.
     * 압축 실패 등으로 일부가 없어도 S3 삭제는 존재 여부와 무관하게 성공 처리된다.
     */
    private Boolean delete(String imageKey) {
        try {
            List<ObjectIdentifier> objectIds = List.of(
                    ObjectIdentifier.builder().key(originalPackagePrefix + imageKey).build(),
                    ObjectIdentifier.builder().key(mainPackagePrefix + imageKey + ".webp").build(),
                    ObjectIdentifier.builder().key(thumbnailPackagePrefix + imageKey + ".webp").build());

            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(objectIds).build())
                    .build());

            return true;
        } catch (S3Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }
}
