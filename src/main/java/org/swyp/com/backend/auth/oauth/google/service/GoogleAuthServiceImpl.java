package org.swyp.com.backend.auth.oauth.google.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.global.exception.ExternalApiConnectionException;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String webClientId;

    @Value("${google.client-id.android}")
    private String androidClientId;

    @Value("${google.client-id.ios}")
    private String iosClientId;

    @Override
    @Transactional
    public SocialAuthResult authenticate(String idToken) {
        Payload payload = verifyIdToken(idToken);

        String sub = payload.getSubject();
        String email = payload.getEmail();

        User user = userRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, sub)
                .orElseGet(() -> userRepository.save(
                        User.createOAuthUser(email, OAuthProvider.GOOGLE, sub, UserRole.USER)
                ));

        return new SocialAuthResult(user.getId(), user.getRole());
    }

    private Payload verifyIdToken(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(webClientId, androidClientId, iosClientId))
                .build();

        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Google idToken입니다.");
            }
            return token.getPayload();
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            throw new ExternalApiConnectionException("Google 토큰 검증 서버와의 연결에 실패했습니다.", "GOOGLE");
        } catch (GeneralSecurityException e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Google idToken입니다.");
        }
    }
}
