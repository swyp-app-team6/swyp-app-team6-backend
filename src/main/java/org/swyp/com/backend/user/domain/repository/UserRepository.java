package org.swyp.com.backend.user.domain.repository;

import java.util.Optional;
import org.swyp.com.backend.user.domain.User;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    User save(User user);
}
