package org.swyp.com.backend.terms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.global.enumeration.TermsType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.support.UserTestFixture;
import org.swyp.com.backend.terms.config.TermsProperties;
import org.swyp.com.backend.terms.domain.TermsAgreement;
import org.swyp.com.backend.terms.domain.repository.TermsAgreementRepository;
import org.swyp.com.backend.terms.dto.TermsAgreementResponse;
import org.swyp.com.backend.terms.dto.TermsListResponse;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TermsServiceTest {

    @Mock
    TermsAgreementRepository termsAgreementRepository;
    @Mock
    UserRepository userRepository;

    TermsService termsService;

    @BeforeEach
    void setUp() {
        termsService = new TermsService(termsAgreementRepository, userRepository, new TermsProperties());
    }

    @Test
    void getTermsList_returnsAllTermsTypes() {
        // when
        TermsListResponse response = termsService.getTermsList();

        // then
        assertThat(response.terms()).hasSize(TermsType.values().length);
        assertThat(response.terms()).allMatch(item -> item.required());
    }

    @Test
    void agreeToTerms_allRequiredTypes_savesAllAndReturnsThem() {
        // given
        User user = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(termsAgreementRepository.save(any(TermsAgreement.class))).thenAnswer(invocation -> {
            TermsAgreement agreement = invocation.getArgument(0);
            ReflectionTestUtils.setField(agreement, "agreedAt", LocalDateTime.now());
            return agreement;
        });

        List<TermsType> allTypes = List.of(TermsType.values());

        // when
        TermsAgreementResponse response = termsService.agreeToTerms(TEST_USER_ID, allTypes);

        // then
        assertThat(response.agreedTypes()).containsExactlyElementsOf(allTypes);
        assertThat(response.agreedAt()).isNotNull();
        verify(termsAgreementRepository, times(allTypes.size())).save(any());
    }

    @Test
    void agreeToTerms_missingRequiredType_throwsBadRequest() {
        // given
        List<TermsType> missingAgeOver14 = List.of(TermsType.SERVICE, TermsType.PRIVACY);

        // when & then
        assertThrows(BusinessException.class, () -> termsService.agreeToTerms(TEST_USER_ID, missingAgeOver14));
        verify(termsAgreementRepository, never()).save(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void hasCompletedRequiredAgreements_noAgreements_returnsFalse() {
        // given
        User user = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        when(termsAgreementRepository.findByUser(user)).thenReturn(List.of());

        // when & then
        assertThat(termsService.hasCompletedRequiredAgreements(user)).isFalse();
    }

    @Test
    void hasCompletedRequiredAgreements_allRequiredAgreedAtCurrentVersion_returnsTrue() {
        // given
        User user = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        List<TermsAgreement> agreements = List.of(
                TermsAgreement.create(user, TermsType.SERVICE, TermsType.SERVICE.getCurrentVersion()),
                TermsAgreement.create(user, TermsType.PRIVACY, TermsType.PRIVACY.getCurrentVersion()),
                TermsAgreement.create(user, TermsType.AGE_OVER_14, TermsType.AGE_OVER_14.getCurrentVersion())
        );
        when(termsAgreementRepository.findByUser(user)).thenReturn(agreements);

        // when & then
        assertThat(termsService.hasCompletedRequiredAgreements(user)).isTrue();
    }

    @Test
    void hasCompletedRequiredAgreements_agreedAtOlderVersion_returnsFalse() {
        // given
        User user = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        List<TermsAgreement> agreements = List.of(
                TermsAgreement.create(user, TermsType.SERVICE, TermsType.SERVICE.getCurrentVersion()),
                TermsAgreement.create(user, TermsType.PRIVACY, 0),
                TermsAgreement.create(user, TermsType.AGE_OVER_14, TermsType.AGE_OVER_14.getCurrentVersion())
        );
        when(termsAgreementRepository.findByUser(user)).thenReturn(agreements);

        // when & then
        assertThat(termsService.hasCompletedRequiredAgreements(user)).isFalse();
    }
}
