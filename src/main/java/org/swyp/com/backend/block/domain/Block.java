package org.swyp.com.backend.block.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.swyp.com.backend.user.domain.User;

@Entity
@Table(name = "block", uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_user_id", "blocked_user_id"}))
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "blocker_user_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User blockerUser;
    @JoinColumn(name = "blocked_user_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User blockedUser;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Block createBlock(User blockerUser, User blockedUser) {
        Block block = new Block();
        block.blockerUser = blockerUser;
        block.blockedUser = blockedUser;
        return block;
    }
}
