package org.swyp.com.backend.cosmic.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

public interface CosmicRepository extends JpaRepository<Cosmic, Long> {
    Optional<Cosmic> findByType(CosmicDatingType cosmicDatingType);
}
