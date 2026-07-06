package org.swyp.com.backend.auth.local.service;

import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;

public interface LocalAuthService {

    SocialAuthResult signup(String email, String rawPassword);

    SocialAuthResult login(String email, String rawPassword);
}
