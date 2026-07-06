package org.swyp.com.backend.report.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.domain.repository.ProfileExchangeRepository;
import org.swyp.com.backend.global.enumeration.ReportReasonCode;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.report.domain.Report;
import org.swyp.com.backend.report.domain.repository.ReportRepository;
import org.swyp.com.backend.report.dto.ReportCreateRequest;
import org.swyp.com.backend.report.dto.ReportResponse;
import org.swyp.com.backend.user.domain.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProfileExchangeRepository profileExchangeRepository;

    @Transactional
    public ReportResponse createReport(Long reporterId, ReportCreateRequest request) {
        String normalizedEtcDetail = normalizeEtcDetail(request.reasonCodes(), request.etcDetail());

        ProfileExchange profileExchange = profileExchangeRepository
                .findByIdAndUserId(request.profileExchangeId(), reporterId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "신고할 대상을 찾을 수 없습니다."));

        User reporterUser = profileExchange.getUser();
        User reportedUser = profileExchange.getProfile().getUser();

        Report report = Report.createReport(reporterUser, reportedUser, profileExchange,
                request.reasonCodes(), normalizedEtcDetail);
        Report saved = reportRepository.save(report);

        return new ReportResponse(saved.getId(), saved.getStatus(), saved.getCreatedAt());
    }

    private String normalizeEtcDetail(List<ReportReasonCode> reasonCodes, String etcDetail) {
        if (!reasonCodes.contains(ReportReasonCode.ETC)) {
            return null;
        }
        if (etcDetail == null || etcDetail.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "기타 사유를 선택한 경우 상세 사유를 입력해주세요.");
        }
        if (etcDetail.length() > ReportCreateRequest.ETC_DETAIL_MAX_LENGTH) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "기타 사유는 " + ReportCreateRequest.ETC_DETAIL_MAX_LENGTH + "자를 초과할 수 없습니다.");
        }
        return etcDetail;
    }
}
