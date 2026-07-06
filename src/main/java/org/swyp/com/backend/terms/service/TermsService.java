package org.swyp.com.backend.terms.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.enumeration.TermsType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.terms.config.TermsProperties;
import org.swyp.com.backend.terms.domain.TermsAgreement;
import org.swyp.com.backend.terms.domain.repository.TermsAgreementRepository;
import org.swyp.com.backend.terms.dto.TermsAgreementResponse;
import org.swyp.com.backend.terms.dto.TermsItemResponse;
import org.swyp.com.backend.terms.dto.TermsListResponse;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

    private final TermsAgreementRepository termsAgreementRepository;
    private final UserRepository userRepository;
    private final TermsProperties termsProperties;

    public TermsListResponse getTermsList() {
        List<TermsItemResponse> terms = Arrays.stream(TermsType.values())
                .map(type -> TermsItemResponse.from(type, termsProperties.getContentUrl(type)))
                .toList();
        return new TermsListResponse(terms);
    }

    public boolean hasCompletedRequiredAgreements(User user) {
        Map<TermsType, Integer> latestVersionByType = latestVersionByType(user);
        return Arrays.stream(TermsType.values())
                .filter(TermsType::isRequired)
                .allMatch(type -> latestVersionByType.getOrDefault(type, 0) >= type.getCurrentVersion());
    }

    @Transactional
    public TermsAgreementResponse agreeToTerms(Long userId, List<TermsType> agreedTypes) {
        Set<TermsType> agreedSet = new HashSet<>(agreedTypes);
        List<TermsType> missingRequired = Arrays.stream(TermsType.values())
                .filter(TermsType::isRequired)
                .filter(type -> !agreedSet.contains(type))
                .toList();
        if (!missingRequired.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "필수 약관에 모두 동의해야 합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        LocalDateTime agreedAt = null;
        for (TermsType type : agreedTypes) {
            TermsAgreement saved = termsAgreementRepository.save(TermsAgreement.create(user, type, type.getCurrentVersion()));
            agreedAt = saved.getAgreedAt();
        }

        return new TermsAgreementResponse(agreedTypes, agreedAt);
    }

    private Map<TermsType, Integer> latestVersionByType(User user) {
        List<TermsAgreement> agreements = termsAgreementRepository.findByUser(user);
        return agreements.stream()
                .collect(Collectors.toMap(TermsAgreement::getTermsType, TermsAgreement::getVersion, Math::max));
    }
}
