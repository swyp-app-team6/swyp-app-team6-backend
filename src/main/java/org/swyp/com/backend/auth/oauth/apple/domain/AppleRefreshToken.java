package org.swyp.com.backend.auth.oauth.apple.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AppleRefreshToken {

    @Id
    private Long userId;

    @Convert(converter = AppleRefreshTokenConverter.class)
    @Column(nullable = false)
    private String refreshToken;

    public AppleRefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
