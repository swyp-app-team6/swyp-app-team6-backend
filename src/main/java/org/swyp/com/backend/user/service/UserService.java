package org.swyp.com.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.auth.jwt.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.auth.oauth.apple.service.AppleAuthService;
import org.swyp.com.backend.block.domain.repository.BlockRepository;
import org.swyp.com.backend.global.enumeration.WithdrawalReasonCode;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.service.ProfileService;
import org.swyp.com.backend.report.domain.Report;
import org.swyp.com.backend.report.domain.repository.ReportRepository;
import org.swyp.com.backend.terms.domain.repository.TermsAgreementRepository;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.WithdrawalLog;
import org.swyp.com.backend.user.domain.repository.UserRepository;
import org.swyp.com.backend.user.domain.repository.WithdrawalLogRepository;
import org.swyp.com.backend.user.dto.UserMeResponse;
import org.swyp.com.backend.user.dto.UserWithdrawalRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppleAuthService appleAuthService;
    private final ProfileService profileService;
    private final WithdrawalLogRepository withdrawalLogRepository;
    private final BlockRepository blockRepository;
    private final ReportRepository reportRepository;
    private final TermsAgreementRepository termsAgreementRepository;

    public UserMeResponse getMe(Long userId) {
        return userRepository.findById(userId)
                .map(UserMeResponse::from)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteUser(Long userId, WithdrawalReasonCode reasonCode, String reasonDetail) {
        String normalizedReasonDetail = normalizeReasonDetail(reasonCode, reasonDetail);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        withdrawalLogRepository.save(WithdrawalLog.create(reasonCode, normalizedReasonDetail));
        appleAuthService.revoke(userId);

        profileService.deleteUserProfile(user);
        reportRepository.findByReporterUser(user).forEach(Report::deleteReportedUser);
        reportRepository.findByReportedUser(user).forEach(Report::deleteReporterUser);
        blockRepository.deleteAllByBlockerUser(user);
        blockRepository.deleteAllByBlockedUser(user);

        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);

        termsAgreementRepository.deleteAllByUser(user);

        userRepository.delete(user);
    }

    private String normalizeReasonDetail(WithdrawalReasonCode reasonCode, String reasonDetail) {
        if (reasonCode != WithdrawalReasonCode.ETC) {
            return null;
        }
        if (reasonDetail == null || reasonDetail.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "기타 사유를 선택한 경우 상세 사유를 입력해주세요.");
        }
        if (reasonDetail.length() > UserWithdrawalRequest.REASON_DETAIL_MAX_LENGTH) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "상세 사유는 " + UserWithdrawalRequest.REASON_DETAIL_MAX_LENGTH + "자를 초과할 수 없습니다.");
        }
        return reasonDetail;
    }
}
