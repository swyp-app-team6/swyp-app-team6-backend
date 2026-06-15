package org.swyp.com.backend.login.service;


import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.auth.domain.RefreshToken;
import org.swyp.com.backend.global.auth.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.global.auth.jwt.CustomClaims;
import org.swyp.com.backend.global.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.global.exception.BusinessException;
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

    @Override
    public TokenResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "회원가입 필요"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }

        CustomClaims accessToken = jwtTokenProvider.generateToken("ACCESS", user.getId(), user.getRole());
        CustomClaims refreshToken = jwtTokenProvider.generateToken("REFRESH", user.getId(), user.getRole());

        persistRefreshToken(refreshToken);

        return new TokenResponse(accessToken.getToken(), refreshToken.getToken());
    }

    @Override
    public TokenResponse refreshTokens(String token) {
        CustomClaims claims = jwtTokenProvider.validateToken(token);

        Long userId = claims.getUserId();
        String jti = claims.getJti();
        UserRole role = claims.getRole();

        validateTokenUUID(userId, jti);

        CustomClaims accessToken = jwtTokenProvider.generateToken("ACCESS", userId, role);
        CustomClaims refreshToken = jwtTokenProvider.generateToken("REFRESH", userId, role);

        persistRefreshToken(refreshToken);

        return new TokenResponse(accessToken.getToken(), refreshToken.getToken());
    }

    private void persistRefreshToken(CustomClaims generatedRefreshToken) {
        Long userId = generatedRefreshToken.getUserId();
        String jti = generatedRefreshToken.getJti();
        Date expiresAt = generatedRefreshToken.getExpiresAt();

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository
                .findByUserId(userId);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            refreshToken.setJti(jti);
            refreshToken.setExpiresAt(expiresAt);
        } else {
            RefreshToken refreshToken = new RefreshToken(userId, jti, expiresAt);
            refreshTokenRepository.save(refreshToken);
        }
    }

    private void validateTokenUUID(Long userId, String jti) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository
                .findByUserId(userId);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            if (!refreshToken.getJti().equals(jti)) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED, "refresh Token 값 불일치");
            }
        }
    }
}
