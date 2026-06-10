package org.swyp.com.backend.global.auth.domain.repository;

import java.util.Optional;
import org.swyp.com.backend.global.auth.domain.RefreshToken;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByAccountId(String accountId);

    RefreshToken save(RefreshToken refreshToken);
}
