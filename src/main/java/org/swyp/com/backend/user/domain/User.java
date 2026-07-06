package org.swyp.com.backend.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;

@Entity
@Table(name = "users")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    @Enumerated(EnumType.STRING)
    @Column
    private OAuthProvider provider;
    @Column
    private String providerUserId;
    @Column
    private String password;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static User createOAuthUser(String email, OAuthProvider provider, String providerUserId, UserRole role) {
        User user = new User();
        user.email = email;
        user.provider = provider;
        user.providerUserId = providerUserId;
        user.role = role;
        return user;
    }

    public static User createLocalUser(String email, String encodedPassword, UserRole role) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.provider = OAuthProvider.LOCAL;
        user.role = role;
        return user;
    }
}
