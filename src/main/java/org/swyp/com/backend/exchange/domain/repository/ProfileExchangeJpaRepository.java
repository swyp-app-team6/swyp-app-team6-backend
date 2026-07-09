package org.swyp.com.backend.exchange.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.user.domain.User;

public interface ProfileExchangeJpaRepository extends JpaRepository<ProfileExchange, Long> {
    void deleteByUser(User user);

    List<ProfileExchange> findByProfile(Profile profile);
}
