package org.swyp.com.backend.global.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.global.upload.dto.PresignedUploadResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class S3UploadServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private PresignedPutObjectRequest presignedPutObjectRequest;

    private S3UploadService s3UploadService;

    @BeforeEach
    void setUp() {
        s3UploadService = new S3UploadService(s3Presigner);
        ReflectionTestUtils.setField(s3UploadService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3UploadService, "packageName", "profile/");
    }

    @Test
    void createPresignedUploadUrl_success() throws MalformedURLException {
        // given
        String filename = "testuser";
        String contentType = "image/png";
        URL fakeUrl = new URL("https://test-bucket.s3.amazonaws.com/profile/testuser-uuid");

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(presignedPutObjectRequest);
        when(presignedPutObjectRequest.url()).thenReturn(fakeUrl);

        // when
        PresignedUploadResponse response = s3UploadService.createPresignedUploadUrl(filename, contentType);

        // then
        assertThat(response.uploadUrl()).isEqualTo(fakeUrl.toString());
        assertThat(response.imageKey()).startsWith("profile/" + filename + "-");
    }

    @Test
    void createPresignedUploadUrl_keyUniqueness() throws MalformedURLException {
        // given
        String filename = "testuser";
        String contentType = "image/jpeg";
        URL fakeUrl = new URL("https://test-bucket.s3.amazonaws.com/profile/testuser-uuid");

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(presignedPutObjectRequest);
        when(presignedPutObjectRequest.url()).thenReturn(fakeUrl);

        // when
        PresignedUploadResponse first = s3UploadService.createPresignedUploadUrl(filename, contentType);
        PresignedUploadResponse second = s3UploadService.createPresignedUploadUrl(filename, contentType);

        // then
        assertThat(first.imageKey()).isNotEqualTo(second.imageKey());
    }
}
