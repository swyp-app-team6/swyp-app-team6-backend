package org.swyp.com.backend.block.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.block.domain.Block;
import org.swyp.com.backend.user.domain.User;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockerUserIdAndBlockedUserId(Long blockerUserId, Long blockedUserId);

    List<Block> findAllByBlockerUserIdOrderByCreatedAtDesc(Long blockerUserId);

    Optional<Block> findByIdAndBlockerUserId(Long id, Long blockerUserId);

    List<Block> findAllByBlockerUserIdAndBlockedUserIdIn(Long blockerUserId, List<Long> blockedUserIds);

    void deleteAllByBlockerUser(User user);

    void deleteAllByBlockedUser(User user);
}
