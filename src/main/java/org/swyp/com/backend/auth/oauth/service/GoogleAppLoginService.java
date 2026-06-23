package org.swyp.com.backend.auth.oauth.service;

import org.swyp.com.backend.auth.oauth.dto.GoogleAuthResult;

public interface GoogleAppLoginService {

    GoogleAuthResult authenticate(String idToken);
}
