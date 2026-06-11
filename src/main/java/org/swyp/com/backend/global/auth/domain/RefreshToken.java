package org.swyp.com.backend.global.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Setter(AccessLevel.NONE)
    private Long userId;
    @Column(nullable = false, unique = true)
    private String jti;
    @Column(nullable = false)
    private Date expiresAt;
}
