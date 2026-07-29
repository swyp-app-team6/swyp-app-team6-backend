package org.swyp.com.backend.upload.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.swyp.com.backend.upload.domain.PendingImageConfirm;
import org.swyp.com.backend.upload.domain.repository.PendingImageConfirmRepository;

/**
 * 압축 Lambda가 아직 main/thumbnail을 만들지 못해 확정 태깅에 실패한 이미지를 주기적으로 재시도한다.
 * 24시간 넘게 확정되지 않은 건은 원본 자체가 Lifecycle Rule로 정리됐을 가능성이 높아 재시도 대상에서 제외한다.
 */
@Component
@RequiredArgsConstructor
public class PendingImageConfirmScheduler {

    private final S3ImageTagService s3ImageTagService;
    private final PendingImageConfirmRepository pendingImageConfirmRepository;

    @Scheduled(cron = "0 */5 * * * *")
    public void retryPendingConfirms() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<PendingImageConfirm> pendingList =
                pendingImageConfirmRepository.findAllByConfirmedFalseAndCreatedAtAfter(cutoff);

        for (PendingImageConfirm pending : pendingList) {
            s3ImageTagService.retryPendingConfirm(pending);
        }
    }
}
