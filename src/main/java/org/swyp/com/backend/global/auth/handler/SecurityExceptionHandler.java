package org.swyp.com.backend.global.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.swyp.com.backend.global.log.dto.ErrorLog;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        ErrorLog errorLog = ErrorLog.createClientErrorLog(HttpStatus.UNAUTHORIZED.value(), e,
                HttpStatus.UNAUTHORIZED.name());
        log.warn(Markers.appendEntries(errorLog.fields()), errorLog.summary());

        writeError(response, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException {
        ErrorLog errorLog = ErrorLog.createClientErrorLog(HttpStatus.FORBIDDEN.value(), e,
                HttpStatus.FORBIDDEN.name());
        log.warn(Markers.appendEntries(errorLog.fields()), errorLog.summary());

        writeError(response, HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String detail) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(status.name());
        problemDetail.setDetail(detail);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }
}
