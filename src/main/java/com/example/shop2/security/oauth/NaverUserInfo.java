package com.example.shop2.security.oauth;

import java.util.Map;

/**
 * 네이버가 보내주는 정보 모양:
 * {
 *   "resultcode": "00",
 *   "message": "success",
 *   "response": {               ← 실제 정보는 response 안에 한 번 더 들어 있어요
 *     "id": "abcDEF123...",
 *     "name": "김강민",
 *     "email": "xxx@naver.com"
 *   }
 * }
 */
public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> response;

    @SuppressWarnings("unchecked")
    public NaverUserInfo(Map<String, Object> attributes) {
        // response 안쪽 Map을 꺼내서 저장
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(response.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getName() {
        return (String) response.get("name");
    }
}