package org.swyp.com.backend.global.auth.jwt;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.swyp.com.backend.global.enumeration.UserRole;

@Getter
@AllArgsConstructor
public class CustomClaims {

    private Long userId;
    private String token;
    private List<UserRole> roles;
    private String jti;
    private Date issuedAt;
    private Date expiresAt;
}
