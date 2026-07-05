package org.swyp.com.backend.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.swyp.com.backend.global.enumeration.WithdrawalReasonCode;

@Entity
@Table(name = "withdrawal_log")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class WithdrawalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalReasonCode reasonCode;
    @Column(length = 300)
    private String reasonDetail;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static WithdrawalLog create(WithdrawalReasonCode reasonCode, String reasonDetail) {
        WithdrawalLog withdrawalLog = new WithdrawalLog();
        withdrawalLog.reasonCode = reasonCode;
        withdrawalLog.reasonDetail = reasonDetail;
        return withdrawalLog;
    }
}
