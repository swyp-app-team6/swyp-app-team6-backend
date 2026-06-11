package org.swyp.com.backend.global.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.global.auth.domain.RefreshToken;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

}
