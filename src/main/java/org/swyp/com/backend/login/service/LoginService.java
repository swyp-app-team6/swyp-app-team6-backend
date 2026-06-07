package org.swyp.com.backend.login.service;

import java.util.List;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.login.dto.TokenResponse;

public interface LoginService {
    public TokenResponse login(String email, String password);

    public TokenResponse refreshTokens(String token, List<UserRole> roles);
}
