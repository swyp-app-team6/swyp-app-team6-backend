package org.swyp.com.backend.auth.oauth.google.service;

import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;

public interface GoogleAuthService {

    SocialAuthResult authenticate(String idToken);
}
