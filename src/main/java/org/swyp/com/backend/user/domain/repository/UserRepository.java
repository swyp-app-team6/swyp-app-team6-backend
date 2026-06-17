package org.swyp.com.backend.user.domain.repository;

import java.util.Optional;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.user.domain.User;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    User save(User user);
}
