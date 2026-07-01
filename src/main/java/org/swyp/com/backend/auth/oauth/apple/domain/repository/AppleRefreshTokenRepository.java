package org.swyp.com.backend.auth.oauth.apple.domain.repository;

import java.util.Optional;
import org.swyp.com.backend.auth.oauth.apple.domain.AppleRefreshToken;

public interface AppleRefreshTokenRepository {

    Optional<AppleRefreshToken> findByUserId(Long userId);

    AppleRefreshToken save(AppleRefreshToken appleRefreshToken);

    void delete(AppleRefreshToken appleRefreshToken);
}
