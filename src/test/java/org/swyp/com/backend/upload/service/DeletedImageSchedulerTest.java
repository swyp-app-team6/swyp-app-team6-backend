package org.swyp.com.backend.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.upload.domain.DeletedImage;
import org.swyp.com.backend.upload.domain.repository.DeletedImageRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;

@ExtendWith(MockitoExtension.class)
class DeletedImageSchedulerTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private DeletedImageRepository deletedImageRepository;

    private DeletedImageScheduler deletedImageScheduler;

    @BeforeEach
    void setUp() {
        deletedImageScheduler = new DeletedImageScheduler(s3Client, deletedImageRepository);
        ReflectionTestUtils.setField(deletedImageScheduler, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(deletedImageScheduler, "originalPackagePrefix", "original/");
        ReflectionTestUtils.setField(deletedImageScheduler, "mainPackagePrefix", "main/");
        ReflectionTestUtils.setField(deletedImageScheduler, "thumbnailPackagePrefix", "thumbnail/");
    }

    @Test
    void deleteImages_targetsCompressedFilesWithWebpExtension() {
        // given
        String imageKey = "testuser-uuid";
        DeletedImage deletedImage = DeletedImage.toDeleteSchedule(imageKey);
        when(deletedImageRepository.findAllByDeletedFalse()).thenReturn(List.of(deletedImage));

        // when
        deletedImageScheduler.deleteImages();

        // then
        ArgumentCaptor<DeleteObjectsRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
        verify(s3Client).deleteObjects(requestCaptor.capture());

        assertThat(requestCaptor.getValue().delete().objects())
                .extracting(id -> id.key())
                .containsExactlyInAnyOrder("original/" + imageKey, "main/" + imageKey + ".webp",
                        "thumbnail/" + imageKey + ".webp");
        assertThat(deletedImage.getDeleted()).isTrue();
        verify(deletedImageRepository).save(deletedImage);
    }
}
