package org.swyp.com.backend.login.service;


import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.auth.JwtTokenProvider.CustomClaims;
import org.swyp.com.backend.global.auth.RefreshToken;
import org.swyp.com.backend.global.auth.RefreshTokenRepository;
import org.swyp.com.backend.global.auth.TokenProvider;
import org.swyp.com.backend.global.exception.LoginException;
import org.swyp.com.backend.login.dto.TokenResponse;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider jwtTokenProvider;

    /**
     * 사용자 로그인을 수행하고 JWT Access Token / Refresh Token을 발급한다.
     *
     * <p>로그인 처리 흐름은 다음과 같다:
     * <ol>
     *   <li>이메일로 사용자 조회</li>
     *   <li>비밀번호 일치 여부 검증</li>
     *   <li>JWT Access Token 및 Refresh Token 발급</li>
     *   <li>Refresh Token을 RTR 정책에 따라 저장 또는 갱신</li>
     *   <li>토큰 정보를 응답으로 반환</li>
     * </ol>
     *
     * <p>비즈니스 규칙:
     * <ul>
     *   <li>존재하지 않는 이메일인 경우 회원가입이 필요하다는 예외 발생</li>
     *   <li>비밀번호가 일치하지 않는 경우 인증 실패 예외 발생</li>
     *   <li>Refresh Token은 RTR(Refresh Token Rotation) 정책에 따라 관리</li>
     * </ul>
     *
     * @param email    사용자 이메일 (로그인 ID)
     * @param password 사용자 비밀번호 (평문 입력)
     * @return Access Token과 Refresh Token이 포함된 TokenResponse
     * @throws LoginException 회원이 존재하지 않거나 비밀번호가 일치하지 않을 경우
     */
    @Override
    public TokenResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LoginException("회원가입 필요"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new LoginException("비밀번호 불일치");
        }

        CustomClaims accessToken = jwtTokenProvider.generateToken("ACCESS", email, user.getRole());
        CustomClaims refreshToken = jwtTokenProvider.generateToken("REFRESH", email, user.getRole());

        persistRefreshToken(refreshToken);

        return new TokenResponse(accessToken.getToken(), refreshToken.getToken());
    }

    @Override
    public TokenResponse refreshTokens(String accountId, String[] roles) { // 유효성은 필터에서 확인하였다고 가정
        CustomClaims accessToken = jwtTokenProvider.generateToken("ACCESS", accountId, roles);
        CustomClaims refreshToken = jwtTokenProvider.generateToken("REFRESH", accountId, roles);

        persistRefreshToken(refreshToken);

        return new TokenResponse(accessToken.getToken(), refreshToken.getToken());
    }

    /**
     * Refresh Token을 RTR(Refresh Token Rotation) 정책에 따라 저장하거나 갱신한다.
     *
     * <p>처리 흐름:
     * <ul>
     *   <li>계정 ID 기준으로 기존 Refresh Token 존재 여부 조회</li>
     *   <li>이미 존재하면 토큰 값과 만료 시간을 갱신</li>
     *   <li>존재하지 않으면 새로운 Refresh Token 엔티티를 생성하여 저장</li>
     * </ul>
     *
     * <p>RTR 정책:
     * <ul>
     *   <li>로그인 시마다 기존 Refresh Token을 폐기하고 새로운 토큰으로 교체</li>
     *   <li>동일 계정에 대해 하나의 유효한 Refresh Token만 유지</li>
     * </ul>
     *
     * @param generatedRefreshToken JWT로부터 생성된 Refresh Token 정보 (accountId, token, expiresAt 포함)
     */
    private void persistRefreshToken(CustomClaims generatedRefreshToken) {
        String accountId = generatedRefreshToken.getAccountId();
        String token = generatedRefreshToken.getToken();
        Date expiresAt = generatedRefreshToken.getExpiresAt();

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository
                .findByAccountId(accountId);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            refreshToken.setToken(token);
            refreshToken.setExpiresAt(expiresAt);
        } else {
            RefreshToken refreshToken = new RefreshToken(accountId, token, expiresAt);
            refreshTokenRepository.save(refreshToken);
        }
    }
}
