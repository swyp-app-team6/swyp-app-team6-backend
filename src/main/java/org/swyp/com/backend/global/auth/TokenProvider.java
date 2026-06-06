package org.swyp.com.backend.global.auth;

import org.swyp.com.backend.global.auth.JwtTokenProvider.CustomClaims;

public interface TokenProvider {
    CustomClaims generateToken(String type, String sub, String[] role);
    CustomClaims validateToken(String token);
}
