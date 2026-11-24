package com.example.shop2.security.oauth;

import java.util.Map;

/**
 * 구글이 보내주는 정보 모양:
 * {
 *   "sub": "115892012345678901234",
 *   "name": "김강민",
 *   "email": "xxx@gmail.com",
 *   ...
 * }
 */
public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}