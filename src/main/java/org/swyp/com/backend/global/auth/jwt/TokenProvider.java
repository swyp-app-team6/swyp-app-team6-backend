package org.swyp.com.backend.global.auth.jwt;

import org.swyp.com.backend.global.enumeration.UserRole;

/**
 * 인증 토큰의 생성 및 검증을 담당하는 서비스 인터페이스입니다.
 */
public interface TokenProvider {

    /**
     * 토큰 타입, 사용자 식별자 및 권한 정보를 사용하여 새로운 토큰을 생성합니다.
     *
     * @param type  생성할 토큰의 타입 (예: "ACCESS", "REFRESH")
     * @param userId 토큰의 주체 (사용자 PK)
     * @param role   사용자 권한
     * @return 생성된 토큰 문자열과 관련 클레임 정보를 포함하는 {@link CustomClaims} 객체
     * @throws io.jsonwebtoken.JwtException 토큰 생성 과정에서 오류가 발생한 경우
     */
    CustomClaims generateToken(String type, Long userId, UserRole role);

    /**
     * 전달받은 토큰의 유효성을 검증하고 토큰에 포함된 클레임 정보를 추출합니다.
     *
     * @param token 검증할 토큰 문자열
     * @return 토큰에서 추출한 사용자 정보 및 클레임 정보를 포함하는 {@link CustomClaims} 객체
     * @throws io.jsonwebtoken.ExpiredJwtException         토큰이 만료된 경우
     * @throws io.jsonwebtoken.security.SignatureException 토큰 서명이 유효하지 않은 경우
     * @throws io.jsonwebtoken.MalformedJwtException       토큰 형식이 올바르지 않은 경우
     * @throws io.jsonwebtoken.UnsupportedJwtException     지원되지 않는 토큰 형식인 경우
     * @throws io.jsonwebtoken.JwtException                기타 토큰 검증 과정에서 오류가 발생한 경우
     */
    CustomClaims validateToken(String token);
}
