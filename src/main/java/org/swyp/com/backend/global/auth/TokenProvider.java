package org.swyp.com.backend.global.auth;

import java.util.List;
import org.swyp.com.backend.global.auth.JwtTokenProvider.CustomClaims;
import org.swyp.com.backend.global.enumeration.UserRole;

public interface TokenProvider {
    CustomClaims generateToken(String type, String sub, List<UserRole> roles);

    CustomClaims validateToken(String token);
}
