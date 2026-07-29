package org.swyp.com.backend.upload.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.upload.domain.PendingImageConfirm;

public interface PendingImageConfirmRepository extends JpaRepository<PendingImageConfirm, Long> {
    List<PendingImageConfirm> findAllByConfirmedFalseAndCreatedAtAfter(LocalDateTime cutoff);
}
