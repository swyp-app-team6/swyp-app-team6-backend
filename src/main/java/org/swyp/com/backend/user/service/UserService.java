package org.swyp.com.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.auth.jwt.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;
import org.swyp.com.backend.user.dto.UserMeResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileInterestRepository profileInterestRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserMeResponse getMe(Long userId) {
        return userRepository.findById(userId)
                .map(UserMeResponse::from)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        profileRepository.findByUser(user)
                .ifPresent(profile -> {
                    profileInterestRepository.deleteByProfile(profile);
                    profileRepository.delete(profile);
                });

        // 추가적인 데이터 제거

        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshToken -> {
                    refreshTokenRepository.delete(refreshToken);
                });

        userRepository.delete(user);
    }
}
