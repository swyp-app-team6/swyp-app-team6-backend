package org.swyp.com.backend.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.upload.domain.PendingImageConfirm;
import org.swyp.com.backend.upload.domain.repository.PendingImageConfirmRepository;
import org.swyp.com.backend.upload.event.ImageUploadConfirmedEvent;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class S3ImageTagServiceTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private PendingImageConfirmRepository pendingImageConfirmRepository;

    private S3ImageTagService s3ImageTagService;

    @BeforeEach
    void setUp() {
        s3ImageTagService = new S3ImageTagService(s3Client, pendingImageConfirmRepository);
        ReflectionTestUtils.setField(s3ImageTagService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(s3ImageTagService, "originalPackagePrefix", "original/");
        ReflectionTestUtils.setField(s3ImageTagService, "mainPackagePrefix", "main/");
        ReflectionTestUtils.setField(s3ImageTagService, "thumbnailPackagePrefix", "thumbnail/");
    }

    @Test
    void onImageUploadConfirmed_tagsAllThreePhysicalFiles() {
        // given
        String imageKey = "testuser-uuid";

        // when
        s3ImageTagService.onImageUploadConfirmed(new ImageUploadConfirmedEvent(imageKey));

        // then
        ArgumentCaptor<PutObjectTaggingRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectTaggingRequest.class);
        verify(s3Client, times(3)).putObjectTagging(requestCaptor.capture());

        assertThat(requestCaptor.getAllValues())
                .extracting(PutObjectTaggingRequest::key)
                .containsExactlyInAnyOrder("original/" + imageKey, "main/" + imageKey + ".webp",
                        "thumbnail/" + imageKey + ".webp");
        verify(pendingImageConfirmRepository, never()).save(any());
    }

    @Test
    void onImageUploadConfirmed_queuesRetryWhenDerivativeNotYetCreated() {
        // given: main/thumbnail은 압축 Lambda가 아직 안 끝나서 NoSuchKey로 실패
        String imageKey = "testuser-uuid";

        lenient().when(s3Client.putObjectTagging(argThat(
                (PutObjectTaggingRequest req) -> req != null && !req.key().equals("original/" + imageKey))))
                .thenThrow(S3Exception.builder().statusCode(404).message("NoSuchKey").build());

        // when
        s3ImageTagService.onImageUploadConfirmed(new ImageUploadConfirmedEvent(imageKey));

        // then
        ArgumentCaptor<PendingImageConfirm> pendingCaptor = ArgumentCaptor.forClass(PendingImageConfirm.class);
        verify(pendingImageConfirmRepository).save(pendingCaptor.capture());
        assertThat(pendingCaptor.getValue().getImageKey()).isEqualTo(imageKey);
    }

    @Test
    void retryPendingConfirm_marksCompleteWhenAllSucceed() {
        // given
        PendingImageConfirm pending = PendingImageConfirm.toRetrySchedule("testuser-uuid");

        // when
        s3ImageTagService.retryPendingConfirm(pending);

        // then
        assertThat(pending.getConfirmed()).isTrue();
        verify(pendingImageConfirmRepository).save(pending);
    }

    @Test
    void retryPendingConfirm_staysIncompleteWhenStillFailing() {
        // given
        PendingImageConfirm pending = PendingImageConfirm.toRetrySchedule("testuser-uuid");
        when(s3Client.putObjectTagging(any(PutObjectTaggingRequest.class)))
                .thenThrow(S3Exception.builder().statusCode(404).message("NoSuchKey").build());

        // when
        s3ImageTagService.retryPendingConfirm(pending);

        // then
        assertThat(pending.getConfirmed()).isFalse();
        verify(pendingImageConfirmRepository, never()).save(any());
    }
}
