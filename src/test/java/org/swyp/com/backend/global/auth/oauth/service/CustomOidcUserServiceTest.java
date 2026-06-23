package org.swyp.com.backend.global.auth.oauth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.swyp.com.backend.auth.oauth.CustomOidcUser;
import org.swyp.com.backend.auth.oauth.service.CustomOidcUserService;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.support.JwtTestFixture;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomOidcUserServiceTest {

    @Mock
    UserRepository userRepository;

    CustomOidcUserService customOidcUserService;

    String sub = OAuthProvider.GOOGLE.toString();
    String email = "test@email.com";

    @BeforeEach
    void setup() {
        customOidcUserService = new CustomOidcUserService(userRepository);
    }

    @Test
    void processOidcUser_createNewOauthUser() {
        //given
        OidcUserRequest oidcUserRequest = createOidcUserRequest(sub, email);
        OidcUser oidcUser = createOidcUser(sub, email);

        String registrationId = oidcUserRequest.getClientRegistration().getRegistrationId();
        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());
        when(userRepository.findByProviderAndProviderUserId(provider, sub))
                .thenReturn(Optional.empty());

        User savedUser = User.createOAuthUser(email, OAuthProvider.GOOGLE, sub, UserRole.USER);
        setUserId(savedUser, JwtTestFixture.TEST_USER_ID);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        CustomOidcUser actual = (CustomOidcUser) customOidcUserService.processOidcUser(oidcUserRequest, oidcUser);

        //then
        assertThat(actual.userId()).isEqualTo(JwtTestFixture.TEST_USER_ID);
        assertThat(actual.role()).isEqualTo(UserRole.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void processOidcUser_existOauthUser() {
        //given
        String sub = OAuthProvider.GOOGLE.toString();
        String email = "test@email.com";
        OidcUserRequest oidcUserRequest = createOidcUserRequest(sub, email);
        OidcUser oidcUser = createOidcUser(sub, email);

        String registrationId = oidcUserRequest.getClientRegistration().getRegistrationId();
        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());
        User savedUser = User.createOAuthUser(email, OAuthProvider.GOOGLE, sub, UserRole.USER);
        setUserId(savedUser, JwtTestFixture.TEST_USER_ID);
        when(userRepository.findByProviderAndProviderUserId(provider, sub))
                .thenReturn(Optional.of(savedUser));

        //when
        CustomOidcUser actual = (CustomOidcUser) customOidcUserService.processOidcUser(oidcUserRequest, oidcUser);

        //then
        assertThat(actual.userId()).isEqualTo(JwtTestFixture.TEST_USER_ID);
        assertThat(actual.role()).isEqualTo(UserRole.USER);
        verify(userRepository, never()).save(any(User.class));
    }

    private OidcUserRequest createOidcUserRequest(String sub, String email) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("localhost/login/oauth2/code/{registrationId}")
                .authorizationUri("http://localhost/auth")
                .tokenUri("http://localhost/token")
                .userInfoUri("userInfoUri")
                .userNameAttributeName("sub")
                .jwkSetUri("http://localhost/jwks")
                .clientName("Google")
                .scope("openid", "email")
                .build();

        OidcIdToken idToken = getOidcIdToken(sub, email);

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-access-token-value",
                Instant.now(),
                Instant.now().plusSeconds(JwtTestFixture.ACCESS_EXP)
        );

        return new OidcUserRequest(clientRegistration, accessToken, idToken);
    }

    private OidcUser createOidcUser(String sub, String email) {
        OidcIdToken idToken = getOidcIdToken(sub, email);

        OidcUserInfo userInfo = new OidcUserInfo(
                Map.of("sub", sub, "email", email)
        );

        return new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority(UserRole.USER.toString())),
                idToken,
                userInfo
        );
    }

    private void setUserId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private OidcIdToken getOidcIdToken(String sub, String email) {
        return new OidcIdToken(
                "test-id-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("sub", sub, "email", email)
        );
    }
}
