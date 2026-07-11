package org.swyp.com.backend.report.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.global.enumeration.ReportReasonCode;
import org.swyp.com.backend.global.enumeration.ReportStatus;
import org.swyp.com.backend.user.domain.User;

@Entity
@Table(name = "report")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "reporter_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User reporterUser;
    @JoinColumn(name = "reported_user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User reportedUser;
    @JoinColumn(name = "profile_exchange_id")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private ProfileExchange profileExchange;
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReportReason> reasons = new ArrayList<>();
    @Column(length = 300)
    private String etcDetail;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Report createReport(User reporterUser, User reportedUser, ProfileExchange profileExchange,
                                      List<ReportReasonCode> reasonCodes, String etcDetail) {
        Report report = new Report();
        report.reporterUser = reporterUser;
        report.reportedUser = reportedUser;
        report.profileExchange = profileExchange;
        report.etcDetail = etcDetail;
        report.status = ReportStatus.RECEIVED;
        report.reasons = reasonCodes.stream()
                .map(reasonCode -> ReportReason.create(report, reasonCode))
                .toList();
        return report;
    }

    public void deleteProfileExchange() {
        profileExchange = null;
    }

    public void deleteReporterUser() {
        profileExchange = null;
        reporterUser = null;
    }

    public void deleteReportedUser() {
        reportedUser = null;
    }
}
