package org.swyp.com.backend.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_AGE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_BIO;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_COSMIC_TYPE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_GENDER;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_JOB;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_ID;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_NICKNAME;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_REGION_DETAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.domain.repository.ProfileExchangeRepository;
import org.swyp.com.backend.global.enumeration.ReportReasonCode;
import org.swyp.com.backend.global.enumeration.ReportStatus;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.report.domain.Report;
import org.swyp.com.backend.report.domain.repository.ReportRepository;
import org.swyp.com.backend.report.dto.ReportCreateRequest;
import org.swyp.com.backend.report.dto.ReportResponse;
import org.swyp.com.backend.support.ExchangeTestFixture;
import org.swyp.com.backend.support.ProfileTestFixture;
import org.swyp.com.backend.support.UserTestFixture;
import org.swyp.com.backend.user.domain.User;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    private static final Long TEST_PROFILE_EXCHANGE_ID = 10L;
    private static final Long TEST_REPORTED_USER_ID = 2L;

    @Mock
    ReportRepository reportRepository;
    @Mock
    ProfileExchangeRepository profileExchangeRepository;

    ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(reportRepository, profileExchangeRepository);
    }

    private ProfileExchange givenProfileExchange() {
        User reporter = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        User reported = UserTestFixture.createUser(TEST_REPORTED_USER_ID, "other@example.com", TEST_ROLE);
        Profile profile = ProfileTestFixture.createProfile(TEST_PROFILE_ID, reported, TEST_PROFILE_NICKNAME,
                TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);
        return ExchangeTestFixture.createProfileExchange(TEST_PROFILE_EXCHANGE_ID, reporter, profile, null, null, null);
    }

    @Test
    void createReport_singleReason_success() {
        // given
        ProfileExchange profileExchange = givenProfileExchange();
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.of(profileExchange));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.FAKE_PROFILE), null);

        // when
        ReportResponse response = reportService.createReport(TEST_USER_ID, request);

        // then
        assertThat(response.status()).isEqualTo(ReportStatus.RECEIVED);

        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());
        Report saved = captor.getValue();
        assertThat(saved.getReporterUser().getId()).isEqualTo(TEST_USER_ID);
        assertThat(saved.getReportedUser().getId()).isEqualTo(TEST_REPORTED_USER_ID);
        assertThat(saved.getReasons()).hasSize(1);
        assertThat(saved.getEtcDetail()).isNull();
    }

    @Test
    void createReport_multipleReasons_success() {
        // given
        ProfileExchange profileExchange = givenProfileExchange();
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.of(profileExchange));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.INAPPROPRIATE_LANGUAGE, ReportReasonCode.FRAUD_OR_MONEY_REQUEST), null);

        // when
        reportService.createReport(TEST_USER_ID, request);

        // then
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());
        assertThat(captor.getValue().getReasons()).hasSize(2);
    }

    @Test
    void createReport_etcReason_savesEtcDetail() {
        // given
        ProfileExchange profileExchange = givenProfileExchange();
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.of(profileExchange));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.ETC), "다른 이유가 있어요");

        // when
        reportService.createReport(TEST_USER_ID, request);

        // then
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());
        assertThat(captor.getValue().getEtcDetail()).isEqualTo("다른 이유가 있어요");
    }

    @Test
    void createReport_etcReason_missingDetail_throwsBadRequest() {
        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.ETC), null);

        assertThrows(BusinessException.class, () -> reportService.createReport(TEST_USER_ID, request));
        verify(reportRepository, never()).save(any());
        verify(profileExchangeRepository, never()).findByIdAndUserId(any(), any());
    }

    @Test
    void createReport_etcReason_blankDetail_throwsBadRequest() {
        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.ETC), "   ");

        assertThrows(BusinessException.class, () -> reportService.createReport(TEST_USER_ID, request));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void createReport_etcReason_detailOver300Chars_throwsBadRequest() {
        String tooLongDetail = "a".repeat(301);
        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.ETC), tooLongDetail);

        assertThrows(BusinessException.class, () -> reportService.createReport(TEST_USER_ID, request));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void createReport_nonEtcReason_ignoresEtcDetail() {
        // given
        ProfileExchange profileExchange = givenProfileExchange();
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.of(profileExchange));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.FAKE_PROFILE), "이 텍스트는 무시되어야 함");

        // when
        reportService.createReport(TEST_USER_ID, request);

        // then
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(captor.capture());
        assertThat(captor.getValue().getEtcDetail()).isNull();
    }

    @Test
    void createReport_profileExchangeNotFoundOrNotOwned_throwsNotFound() {
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.empty());

        ReportCreateRequest request = new ReportCreateRequest(TEST_PROFILE_EXCHANGE_ID,
                List.of(ReportReasonCode.FAKE_PROFILE), null);

        assertThrows(BusinessException.class, () -> reportService.createReport(TEST_USER_ID, request));
        verify(reportRepository, never()).save(any());
    }
}
