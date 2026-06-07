package org.swyp.com.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.swyp.com.backend.global.auth.JwtTokenProvider.CustomClaims;
import org.swyp.com.backend.global.auth.TokenProvider;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = resolveToken(request.getHeader("Authorization"));

            if (token != null) {
                // token validate
                CustomClaims claims = tokenProvider.validateToken(token);
                // claims 기반으로 Authentication 생성
                Authentication authentication = getAuthentication(claims);
                // SecurityContext Set
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, "토큰 만료됨");
        } catch (SignatureException e) {
            setErrorResponse(response, "서명 검증 실패");
        } catch (MalformedJwtException e) {
            setErrorResponse(response, "토큰 형식 오류");
        } catch (UnsupportedJwtException e) {
            setErrorResponse(response, "지원되지 않는 토큰");
        } catch (JwtException e) {
            setErrorResponse(response, "JWT 처리 실패");
        }
    }

    private Authentication getAuthentication(CustomClaims claims) {
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(claims.getRoles());
        UserDetails userDetails = new User(claims.getAccountId(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                authorities);

    }

    private String resolveToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        } else {
            return null;
        }
    }

    private void setErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, errorMessage
        );

        objectMapper.writeValue(response.getWriter(), problemDetail);
    }

}
