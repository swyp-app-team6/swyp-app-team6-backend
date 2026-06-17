package org.swyp.com.backend.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.auth.jwt.CustomClaims;
import org.swyp.com.backend.global.auth.service.TokenService;
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
    private final TokenService tokenService;

    @Override
    public TokenResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "회원가입 필요"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }
        return tokenService.issueTokenPair(user.getId(), user.getRole());
    }

    @Override
    public TokenResponse refreshTokens(String token) {
        CustomClaims claims = tokenService.validateToken(token);
        tokenService.verifyRefreshTokenJti(claims.getUserId(), claims.getJti());
        return tokenService.issueTokenPair(claims.getUserId(), claims.getRole());
    }
}
