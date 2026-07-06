package org.swyp.com.backend.report.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
