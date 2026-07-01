package org.swyp.com.backend.auth.oauth.apple.domain.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.swyp.com.backend.auth.oauth.apple.domain.AppleRefreshToken;

@Repository
@RequiredArgsConstructor
public class AppleRefreshTokenRepositoryImpl implements AppleRefreshTokenRepository {

    private final AppleRefreshTokenJpaRepository appleRefreshTokenJpaRepository;

    @Override
    public Optional<AppleRefreshToken> findByUserId(Long userId) {
        return appleRefreshTokenJpaRepository.findById(userId);
    }

    @Override
    public AppleRefreshToken save(AppleRefreshToken appleRefreshToken) {
        return appleRefreshTokenJpaRepository.save(appleRefreshToken);
    }

    @Override
    public void delete(AppleRefreshToken appleRefreshToken) {
        appleRefreshTokenJpaRepository.delete(appleRefreshToken);
    }
}
