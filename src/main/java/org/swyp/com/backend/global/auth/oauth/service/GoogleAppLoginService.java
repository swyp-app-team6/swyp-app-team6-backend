package org.swyp.com.backend.global.auth.oauth.service;

import org.swyp.com.backend.global.auth.oauth.dto.GoogleAuthResult;

public interface GoogleAppLoginService {

    GoogleAuthResult authenticate(String idToken);
}
