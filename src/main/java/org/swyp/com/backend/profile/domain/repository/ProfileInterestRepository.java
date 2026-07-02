package org.swyp.com.backend.profile.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileInterest;

public interface ProfileInterestRepository extends JpaRepository<ProfileInterest, Long> {
    List<ProfileInterest> findByProfile(Profile profile);

    List<ProfileInterest> findByProfileOrderByInterestId(Profile profile);

    void deleteByProfile(Profile profile);
}
