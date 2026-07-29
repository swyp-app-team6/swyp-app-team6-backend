package org.swyp.com.backend.upload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.swyp.com.backend.global.log.dto.ErrorLog;
import org.swyp.com.backend.upload.domain.PendingImageConfirm;
import org.swyp.com.backend.upload.domain.repository.PendingImageConfirmRepository;
import org.swyp.com.backend.upload.event.ImageUploadConfirmedEvent;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

/**
 * 이미지 하나는 original/main/thumbnail 세 개의 물리 파일로 존재한다. main/thumbnail은 압축 Lambda가
 * 비동기로 만들기 때문에, 프로필 저장 확정 시점에 아직 존재하지 않아 태깅이 실패할 수 있다. 이 경우
 * {@link PendingImageConfirm}에 재시도 대상으로 남기고, {@link PendingImageConfirmScheduler}가 주기적으로 재시도한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageTagService {

    private final S3Client s3Client;
    private final PendingImageConfirmRepository pendingImageConfirmRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.bucket.package.original}")
    private String originalPackagePrefix;
    @Value("${aws.s3.bucket.package.main}")
    private String mainPackagePrefix;
    @Value("${aws.s3.bucket.package.thumbnail}")
    private String thumbnailPackagePrefix;

    @Async("imageTagExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onImageUploadConfirmed(ImageUploadConfirmedEvent event) {
        String imageKey = event.imageKey();

        if (!confirmAll(imageKey)) {
            pendingImageConfirmRepository.save(PendingImageConfirm.toRetrySchedule(imageKey));
        }
    }

    public void retryPendingConfirm(PendingImageConfirm pending) {
        if (confirmAll(pending.getImageKey())) {
            pending.completeConfirm();
            pendingImageConfirmRepository.save(pending);
        }
    }

    private boolean confirmAll(String imageKey) {
        boolean original = tagConfirmed(originalPackagePrefix + imageKey);
        boolean main = tagConfirmed(mainPackagePrefix + imageKey + ".webp");
        boolean thumbnail = tagConfirmed(thumbnailPackagePrefix + imageKey + ".webp");
        return original && main && thumbnail;
    }

    private boolean tagConfirmed(String key) {
        try {
            s3Client.putObjectTagging(PutObjectTaggingRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .tagging(Tagging.builder()
                            .tagSet(Tag.builder().key("confirmed").value("true").build())
                            .build())
                    .build());
            return true;
        } catch (S3Exception e) {
            ErrorLog errorLog = ErrorLog.createExternalErrorLog(e.statusCode(), e, "S3_IMAGE_TAGGING");
            log.error(Markers.appendEntries(errorLog.fields()), errorLog.summary());
            return false;
        }
    }
}
