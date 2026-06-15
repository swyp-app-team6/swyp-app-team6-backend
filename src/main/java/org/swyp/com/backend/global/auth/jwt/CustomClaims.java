package org.swyp.com.backend.global.auth.jwt;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.UserRole;

@Getter
@AllArgsConstructor
public class CustomClaims {

    private Long userId;
    private String token;
    private UserRole role;
    private String jti;
    private Date issuedAt;
    private Date expiresAt;
}
