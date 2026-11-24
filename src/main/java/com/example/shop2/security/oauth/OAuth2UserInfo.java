package com.example.shop2.security.oauth;

/**
 * SNS마다 다르게 생긴 사용자 정보를 똑같은 방식으로 꺼내기 위한 "규칙(인터페이스)".
 * 구글, 네이버, 카카오 통역사 클래스가 모두 이 규칙을 따라요.
 */
public interface OAuth2UserInfo {

    // 어느 사이트인지 (google, naver, kakao)
    String getProvider();

    // 그 사이트에서 이 사용자를 구분하는 고유번호
    String getProviderId();

    // 이메일 (사이트 설정에 따라 없을 수도 있어요)
    String getEmail();

    // 이름 또는 닉네임
    String getName();
}