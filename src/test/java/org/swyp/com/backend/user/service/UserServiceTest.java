package org.swyp.com.backend.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyp.com.backend.auth.jwt.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.auth.oauth.apple.service.AppleAuthService;
import org.swyp.com.backend.block.domain.repository.BlockRepository;
import org.swyp.com.backend.global.enumeration.WithdrawalReasonCode;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.service.ProfileService;
import org.swyp.com.backend.report.domain.repository.ReportRepository;
import org.swyp.com.backend.support.UserTestFixture;
import org.swyp.com.backend.terms.domain.repository.TermsAgreementRepository;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.WithdrawalLog;
import org.swyp.com.backend.user.domain.repository.UserRepository;
import org.swyp.com.backend.user.domain.repository.WithdrawalLogRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    AppleAuthService appleAuthService;
    @Mock
    WithdrawalLogRepository withdrawalLogRepository;
    @Mock
    BlockRepository blockRepository;
    @Mock
    ProfileService profileService;
    @Mock
    ReportRepository ReportRepository;
    @Mock
    TermsAgreementRepository termsAgreementRepository;

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, profileRepository, refreshTokenRepository, appleAuthService,
                profileService, withdrawalLogRepository, blockRepository, ReportRepository, termsAgreementRepository);
    }

    @ParameterizedTest
    @EnumSource(value = WithdrawalReasonCode.class, names = "ETC", mode = EnumSource.Mode.EXCLUDE)
    void deleteUserSuccessTest(WithdrawalReasonCode reasonCode) {
        // given
        User user = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(user.getId(), reasonCode, null);

        // then
        ArgumentCaptor<WithdrawalLog> captor = ArgumentCaptor.forClass(WithdrawalLog.class);
        verify(withdrawalLogRepository).save(captor.capture());
        assertThat(captor.getValue().getReasonCode()).isEqualTo(reasonCode);
        assertThat(captor.getValue().getReasonDetail()).isNull();
        verify(appleAuthService).revoke(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserWithEtcReason_savesReasonDetail() {
        // given
        User user = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(user.getId(), WithdrawalReasonCode.ETC, "다른 이유가 있어요");

        // then
        ArgumentCaptor<WithdrawalLog> captor = ArgumentCaptor.forClass(WithdrawalLog.class);
        verify(withdrawalLogRepository).save(captor.capture());
        assertThat(captor.getValue().getReasonCode()).isEqualTo(WithdrawalReasonCode.ETC);
        assertThat(captor.getValue().getReasonDetail()).isEqualTo("다른 이유가 있어요");
    }

    @Test
    void deleteUserWithEtcReason_missingDetail_throwsBadRequest() {
        // when & then
        assertThrows(BusinessException.class, () ->
                userService.deleteUser(TEST_USER_ID, WithdrawalReasonCode.ETC, null));

        verify(withdrawalLogRepository, never()).save(any(WithdrawalLog.class));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void deleteUserWithEtcReason_blankDetail_throwsBadRequest() {
        // when & then
        assertThrows(BusinessException.class, () ->
                userService.deleteUser(TEST_USER_ID, WithdrawalReasonCode.ETC, "   "));

        verify(withdrawalLogRepository, never()).save(any(WithdrawalLog.class));
    }

    @Test
    void deleteUserWithEtcReason_detailOver300Chars_throwsBadRequest() {
        // given
        String tooLongDetail = "a".repeat(301);

        // when & then
        assertThrows(BusinessException.class, () ->
                userService.deleteUser(TEST_USER_ID, WithdrawalReasonCode.ETC, tooLongDetail));

        verify(withdrawalLogRepository, never()).save(any(WithdrawalLog.class));
    }
}