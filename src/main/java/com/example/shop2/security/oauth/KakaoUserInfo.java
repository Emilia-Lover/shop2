package com.example.shop2.security.oauth;

import java.util.Map;

/**
 * 카카오가 보내주는 정보 모양:
 * {
 *   "id": 1234567890,                      ← 숫자예요
 *   "kakao_account": {
 *     "email": "xxx@kakao.com",            ← 앱 설정에 따라 없을 수 있어요
 *     "profile": {
 *       "nickname": "김강민"
 *     }
 *   }
 * }
 */
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        // 숫자(Long)로 오기 때문에 문자열로 바꿔요
        return String.valueOf(attributes.get("id"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getEmail() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account == null) {
            return null;
        }
        return (String) account.get("email");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getName() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account == null) {
            return null;
        }
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        if (profile == null) {
            return null;
        }
        return (String) profile.get("nickname");
    }
}