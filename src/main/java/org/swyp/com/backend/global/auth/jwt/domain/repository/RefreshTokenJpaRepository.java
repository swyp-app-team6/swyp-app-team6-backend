package org.swyp.com.backend.global.auth.jwt.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.global.auth.jwt.domain.RefreshToken;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

}
