package org.swyp.com.backend.login.service;

import org.swyp.com.backend.login.dto.TokenResponse;

/**
 * 사용자 인증 및 토큰 관리를 담당하는 서비스 인터페이스입니다.
 */
public interface LoginService {

    /**
     * 이메일과 비밀번호를 사용하여 사용자 로그인을 처리합니다.
     *
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 생성된 액세스 토큰과 리프레시 토큰 정보를 담은 {@link TokenResponse}
     * @throws org.swyp.com.backend.global.exception.LoginException 사용자를 찾을 수 없거나 비밀번호가 일치하지 않는 경우 발생
     */
    public TokenResponse login(String email, String password);

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     * 기존 리프레시 토큰의 유효성을 검증하고, 성공 시 토큰을 갱신합니다.
     *
     * @param token 현재 유효한 리프레시 토큰
     * @return 새롭게 생성된 액세스 토큰과 리프레시 토큰 정보를 담은 {@link TokenResponse}
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰이 만료된 경우 발생
     * @throws io.jsonwebtoken.security.SignatureException 토큰의 서명이 일치하지 않는 경우 발생
     * @throws io.jsonwebtoken.MalformedJwtException 토큰 형식이 올바르지 않은 경우 발생
     * @throws io.jsonwebtoken.UnsupportedJwtException 지원되지 않는 JWT 토큰인 경우 발생
     * @throws io.jsonwebtoken.JwtException 기타 JWT 관련 검증 오류 발생 시
     * @throws org.swyp.com.backend.global.exception.BusinessException 저장된 리프레시 토큰의 JTI 정보와 일치하지 않는 경우 발생
     */
    public TokenResponse refreshTokens(String token);
}
