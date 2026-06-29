package org.swyp.com.backend.profile.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileShort;

public interface ProfileShortRepository extends JpaRepository<ProfileShort, Long> {
    List<ProfileShort> findByProfile(Profile profile);

    void deleteByProfile(Profile profile);
}
