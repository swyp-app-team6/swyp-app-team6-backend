package org.swyp.com.backend.login.service;

import org.swyp.com.backend.login.dto.TokenResponse;

public interface LoginService {
    public TokenResponse login(String email, String password);
}
