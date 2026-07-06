package org.swyp.com.backend.report.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.ReportReasonCode;

@Entity
@Table(name = "report_reason")
@Getter
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "report_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Report report;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReasonCode reasonCode;

    static ReportReason create(Report report, ReportReasonCode reasonCode) {
        ReportReason reportReason = new ReportReason();
        reportReason.report = report;
        reportReason.reasonCode = reasonCode;
        return reportReason;
    }
}
