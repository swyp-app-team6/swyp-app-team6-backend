package org.swyp.com.backend.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
