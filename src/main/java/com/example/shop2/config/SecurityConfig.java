package com.example.shop2.config;

import com.example.shop2.security.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 보안 설정 (경비실 역할)
 * - 어떤 주소는 로그인 없이 열어줄지
 * - 일반 로그인 / SNS 로그인 화면과 처리 방법
 * - 비밀번호를 어떤 방식으로 암호화할지
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // SNS 로그인 후 회원 정보를 처리할 서비스
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 로그인하지 않아도 들어갈 수 있는 주소 (화면 + CSS, JS, 이미지 같은 정적 파일)
                        .requestMatchers("/", "/join", "/login", "/error",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        // 상품 구경은 누구나 (상품 상세: /products/1, /products/2 ...)
                        .requestMatchers("/products/**").permitAll()
                        // SNS 로그인 과정에서 쓰는 주소
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        // 나머지(마이페이지, 장바구니 등)는 전부 로그인해야 들어갈 수 있음
                        .anyRequest().authenticated()
                )
                // ===== 일반 로그인 =====
                .formLogin(form -> form
                        .loginPage("/login")            // 우리가 만든 로그인 화면
                        .loginProcessingUrl("/login")   // 로그인 폼이 POST로 보낼 주소
                        .defaultSuccessUrl("/")         // 성공 시 이동 (원래 가려던 페이지가 있으면 그쪽으로)
                        .failureUrl("/login?error")     // 실패 시 이동
                        .permitAll()
                )
                // ===== SNS 로그인 =====
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")            // SNS 로그인도 같은 로그인 화면 사용
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error")
                        // SNS에서 받은 정보를 우리 서비스로 처리 → 자동 회원가입
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService)
                        )
                )
                // ===== 로그아웃 =====
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                );

        return http.build();
    }

    /**
     * 비밀번호 암호화 도구.
     * 로그인할 때 Spring Security가 이 도구로 입력값과 DB 값을 비교해요.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}