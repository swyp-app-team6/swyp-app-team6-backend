package org.swyp.com.backend.auth.jwt.domain.repository;

import java.util.Optional;
import org.swyp.com.backend.auth.jwt.domain.RefreshToken;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByUserId(Long userId);

    RefreshToken save(RefreshToken refreshToken);
}
