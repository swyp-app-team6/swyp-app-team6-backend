package org.swyp.com.backend.global.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.swyp.com.backend.auth.jwt.TokenProvider;
import org.swyp.com.backend.auth.jwt.filter.JwtAuthenticationFilter;
import org.swyp.com.backend.auth.oauth.google.handler.OAuth2AuthenticationSuccessHandler;
import org.swyp.com.backend.auth.oauth.google.service.CustomOidcUserService;
import org.swyp.com.backend.global.exception.handler.SecurityExceptionHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/oauth2/google/callback"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ASYNC)
                        .permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/auth/login",
                                "/auth/signup",
                                "/auth/refresh",
                                "/auth/google/app",
                                "/auth/apple/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
