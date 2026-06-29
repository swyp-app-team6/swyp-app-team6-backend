package org.swyp.com.backend.profile.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoice;

public interface ProfileChoiceRepository extends JpaRepository<ProfileChoice, Long> {
    List<ProfileChoice> findByProfile(Profile profile);

    void deleteByProfile(Profile profile);
}
