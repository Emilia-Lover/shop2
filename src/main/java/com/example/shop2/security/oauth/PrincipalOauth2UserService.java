package com.example.shop2.security.oauth;

import com.example.shop2.domain.Member;
import com.example.shop2.mapper.MemberMapper;
import com.example.shop2.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SNS 로그인 성공 후, SNS에서 받은 사용자 정보를 처리하는 곳.
 *
 * 1. SNS에서 사용자 정보 받아오기 (부모 클래스가 대신 해줌)
 * 2. 어느 SNS인지 보고 알맞은 "통역사(UserInfo)" 고르기
 * 3. DB에 이미 있는 회원인지 확인 → 없으면 자동 회원가입
 * 4. PrincipalDetails로 포장해서 돌려주기 → 로그인 완료
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. SNS에서 사용자 정보 받아오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 어느 SNS로 로그인했는지 확인 (google, naver, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("SNS 로그인 시도: {} / 받은 정보: {}", registrationId, oAuth2User.getAttributes());

        OAuth2UserInfo userInfo = switch (registrationId) {
            case "google" -> new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver" -> new NaverUserInfo(oAuth2User.getAttributes());
            case "kakao" -> new KakaoUserInfo(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider"), "지원하지 않는 SNS예요: " + registrationId);
        };

        // 3. 우리 사이트에서 쓸 아이디 만들기 → 예: google_115892012345678901234
        String username = userInfo.getProvider() + "_" + userInfo.getProviderId();

        // 4. 이미 가입한 회원인지 확인
        Member member = memberMapper.findByUsername(username);

        if (member == null) {
            // 처음 온 사람 → 자동 회원가입
            member = new Member();
            member.setUsername(username);
            member.setPassword(null); // SNS 회원은 비밀번호가 없어요
            member.setName(userInfo.getName() != null ? userInfo.getName() : userInfo.getProvider() + " 회원");
            member.setEmail(userInfo.getEmail());
            member.setRole("ROLE_USER");
            member.setProvider(userInfo.getProvider());
            member.setProviderId(userInfo.getProviderId());
            memberMapper.insert(member);

            // 가입일(created_at)은 DB가 채워주니까, 저장된 값을 다시 읽어와요
            member = memberMapper.findByUsername(username);
            log.info("SNS 자동 회원가입 완료: {}", member);
        }

        // 5. 일반 로그인과 같은 포장지(PrincipalDetails)로 돌려주기
        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}