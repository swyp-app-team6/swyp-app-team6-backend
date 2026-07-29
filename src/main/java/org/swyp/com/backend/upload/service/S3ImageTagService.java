package org.swyp.com.backend.upload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.swyp.com.backend.global.log.dto.ErrorLog;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageTagService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;
    @Value("${aws.s3.bucket.package.original}")
    private String originalPackagePrefix;

    @Async("imageTagExecutor")
    public void confirmUpload(String imageKey) {
        String key = originalPackagePrefix + imageKey;

        try {
            s3Client.putObjectTagging(PutObjectTaggingRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .tagging(Tagging.builder()
                            .tagSet(Tag.builder().key("confirmed").value("true").build())
                            .build())
                    .build());
        } catch (S3Exception e) {
            ErrorLog errorLog = ErrorLog.createExternalErrorLog(e.statusCode(), e, "S3_IMAGE_TAGGING");
            log.error(Markers.appendEntries(errorLog.fields()), errorLog.summary());
        }
    }
}
