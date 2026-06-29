package org.swyp.com.backend.profile.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoiceTemplate;

public interface ProfileChoiceTemplateRepository extends JpaRepository<ProfileChoiceTemplate, Long> {
    List<ProfileChoiceTemplate> findByProfile(Profile profile);

    void deleteByProfile(Profile profile);
}
