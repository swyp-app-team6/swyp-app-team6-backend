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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedImageScheduler {
    private final S3Client s3Client;
    private final DeletedImageRepository deletedImageRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

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

    private Boolean delete(String key) {
        try {
            DeleteObjectRequest request =
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();

            s3Client.deleteObject(request);

            return true;
        } catch (S3Exception e) {
            log.info(e.getMessage());
            return false;
        }
    }
}
