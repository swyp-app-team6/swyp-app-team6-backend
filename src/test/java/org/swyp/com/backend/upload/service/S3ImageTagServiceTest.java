package org.swyp.com.backend.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class S3ImageTagServiceTest {

    @Mock
    private S3Client s3Client;

    private S3ImageTagService s3ImageTagService;

    @BeforeEach
    void setUp() {
        s3ImageTagService = new S3ImageTagService(s3Client);
        ReflectionTestUtils.setField(s3ImageTagService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3ImageTagService, "originalPackagePrefix", "original/");
    }

    @Test
    void confirmUpload_tagsObjectWithConfirmedTrue() {
        // given
        String imageKey = "testuser-uuid";

        // when
        s3ImageTagService.confirmUpload(imageKey);

        // then
        ArgumentCaptor<PutObjectTaggingRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectTaggingRequest.class);
        verify(s3Client).putObjectTagging(requestCaptor.capture());

        PutObjectTaggingRequest request = requestCaptor.getValue();
        assertThat(request.bucket()).isEqualTo("test-bucket");
        assertThat(request.key()).isEqualTo("original/" + imageKey);
        assertThat(request.tagging().tagSet()).hasSize(1);
        assertThat(request.tagging().tagSet().get(0).key()).isEqualTo("confirmed");
        assertThat(request.tagging().tagSet().get(0).value()).isEqualTo("true");
    }

    @Test
    void confirmUpload_doesNotThrowWhenS3CallFails() {
        // given
        when(s3Client.putObjectTagging(any(PutObjectTaggingRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(404).message("NoSuchKey").build());

        // when / then
        s3ImageTagService.confirmUpload("missing-key");
    }
}
