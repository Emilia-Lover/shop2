package com.example.shop2.security;

import com.example.shop2.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 로그인한 회원 정보를 담는 "포장지".
 *
 * Spring Security는 우리가 만든 Member 클래스를 모르기 때문에,
 * Spring Security가 아는 모양(UserDetails, OAuth2User)으로 포장해서 건네줘야 해요.
 *
 * - UserDetails : 일반 로그인(아이디/비밀번호)용 모양
 * - OAuth2User  : SNS 로그인용 모양 (4단계에서 사용)
 *
 * 두 가지를 모두 구현해 두면, 일반 로그인이든 SNS 로그인이든
 * 컨트롤러에서는 똑같이 PrincipalDetails 하나로 회원 정보를 꺼낼 수 있어요.
 * (6단계 장바구니에서 "지금 로그인한 회원 번호"를 꺼낼 때 아주 편해져요)
 */
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final Member member;                 // DB에서 꺼낸 회원 정보
    private final Map<String, Object> attributes; // SNS에서 받은 정보 (일반 로그인은 비어 있음)

    // 일반 로그인용 생성자
    public PrincipalDetails(Member member) {
        this(member, Collections.emptyMap());
    }

    // SNS 로그인용 생성자 (4단계에서 사용)
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    // 컨트롤러나 화면에서 회원 정보를 꺼낼 때 사용
    public Member getMember() {
        return member;
    }

    // ===== UserDetails (일반 로그인) =====

    // 이 회원의 권한 목록 (ROLE_USER)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole()));
    }

    // DB에 저장된 암호화된 비밀번호 → Spring Security가 입력값과 비교해요
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // 로그인 아이디
    @Override
    public String getUsername() {
        return member.getUsername();
    }

    // 아래 4개는 "계정 만료, 잠김, 비밀번호 만료, 사용 가능 여부"예요.
    // 지금은 그런 기능이 없으니 모두 true(문제없음)로 둬요.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ===== OAuth2User (SNS 로그인, 4단계에서 사용) =====

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 회원을 구분하는 고유한 이름 → 중복되지 않는 username을 사용
    @Override
    public String getName() {
        return member.getUsername();
    }
}