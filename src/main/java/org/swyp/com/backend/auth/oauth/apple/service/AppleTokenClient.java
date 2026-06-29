package org.swyp.com.backend.auth.oauth.apple.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.swyp.com.backend.auth.oauth.apple.config.AppleProperties;
import org.swyp.com.backend.auth.oauth.apple.dto.AppleTokenResponse;
import org.swyp.com.backend.global.exception.ExternalApiConnectionException;

@Component
@RequiredArgsConstructor
public class AppleTokenClient {

    private final AppleClientSecretGenerator clientSecretGenerator;
    private final AppleProperties appleProperties;

    private static final RestClient restClient = RestClient.builder()
            .baseUrl("https://appleid.apple.com")
            .build();

    public AppleTokenResponse exchangeCode(String authorizationCode) {
        try {
            String clientSecret = clientSecretGenerator.generate();

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("client_id", appleProperties.getClientId());
            form.add("client_secret", clientSecret);
            form.add("code", authorizationCode);
            form.add("grant_type", "authorization_code");
            form.add("redirect_uri", appleProperties.getRedirectUri());

            return restClient.post()
                    .uri("/auth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(AppleTokenResponse.class);
        } catch (org.springframework.web.client.RestClientResponseException e) {
            throw new ExternalApiConnectionException(
                    "Apple 토큰 교환 실패 (status=" + e.getStatusCode() + ", body=" + e.getResponseBodyAsString() + ")", "APPLE");
        } catch (Exception e) {
            throw new ExternalApiConnectionException("Apple 토큰 교환 서버와의 연결에 실패했습니다.", "APPLE");
        }
    }
}
