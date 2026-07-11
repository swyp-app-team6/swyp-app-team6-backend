package org.swyp.com.backend.upload.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.upload.domain.DeletedImage;

public interface DeletedImageRepository extends JpaRepository<DeletedImage, Long> {
}
