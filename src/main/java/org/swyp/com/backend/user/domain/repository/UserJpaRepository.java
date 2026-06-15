package org.swyp.com.backend.user.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.user.domain.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
}
