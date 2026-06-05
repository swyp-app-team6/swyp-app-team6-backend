package org.swyp.com.backend.login.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.login.dto.TokenResponse;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse login(String email, String password) {
        return new TokenResponse("accessToken", "refreshToken");
    }
}
