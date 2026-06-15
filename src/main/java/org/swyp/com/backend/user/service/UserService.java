package org.swyp.com.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.user.domain.repository.UserRepository;
import org.swyp.com.backend.user.dto.UserMeResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserMeResponse getMe(Long userId) {
        return userRepository.findById(userId)
                .map(UserMeResponse::from)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }
}
