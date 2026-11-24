package com.example.shop2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * member 테이블의 한 줄(회원 한 명)을 담는 클래스.
 * DB 컬럼 이름(provider_id)과 Java 변수 이름(providerId)은
 * application.properties의 map-underscore-to-camel-case 설정 덕분에 자동으로 연결돼요.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "password") // 로그에 비밀번호가 찍히지 않도록 제외
public class Member {

    private Long id;                  // 회원 번호
    private String username;          // 로그인 아이디
    private String password;          // 암호화된 비밀번호
    private String name;              // 이름
    private String email;             // 이메일
    private String role;              // 권한 (ROLE_USER)
    private String provider;          // 가입 경로 (local, google, naver, kakao)
    private String providerId;        // SNS 고유 번호
    private LocalDateTime createdAt;  // 가입일
}