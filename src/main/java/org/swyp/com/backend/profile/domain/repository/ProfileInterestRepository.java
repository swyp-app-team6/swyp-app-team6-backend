package org.swyp.com.backend.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.profile.domain.ProfileInterest;

public interface ProfileInterestRepository extends JpaRepository<ProfileInterest, Long> {
}
