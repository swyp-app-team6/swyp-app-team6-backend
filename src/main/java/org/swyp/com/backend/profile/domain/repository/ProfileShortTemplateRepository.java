package org.swyp.com.backend.profile.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileShortTemplate;

public interface ProfileShortTemplateRepository extends JpaRepository<ProfileShortTemplate, Long> {
    List<ProfileShortTemplate> findByProfile(Profile profile);

    void deleteByProfile(Profile profile);
}
