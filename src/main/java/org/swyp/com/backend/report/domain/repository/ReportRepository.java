package org.swyp.com.backend.report.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.report.domain.Report;
import org.swyp.com.backend.user.domain.User;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReporterUser(User user);

    List<Report> findByReportedUser(User user);
}
