package org.swyp.com.backend.cosmic.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.cosmic.domain.CosmicTypeTest;

public interface CosmicTypeTestRepository extends JpaRepository<CosmicTypeTest, Long> {
    List<CosmicTypeTest> findByDeletedFalse();
}
