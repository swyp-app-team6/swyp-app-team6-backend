package org.swyp.com.backend.global.auth;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByAccountId(String accountId);

    RefreshToken save(RefreshToken refreshToken);
}
