package org.swyp.com.backend.profile.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.user.domain.User;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserAndDeletedFalse(User user);

    Optional<Profile> findByQrAndDeletedFalse(UUID uuid);

    Optional<Profile> findByIdAndDeletedFalse(Long id);

    List<Profile> findByUser(User user);
}
