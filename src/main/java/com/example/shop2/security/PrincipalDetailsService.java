package com.example.shop2.security;

import com.example.shop2.domain.Member;
import com.example.shop2.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 로그인할 때 Spring Security가 자동으로 부르는 클래스.
 *
 * 로그인 버튼을 누르면 Spring Security가
 * "qwer라는 아이디를 가진 회원 좀 찾아줘" 하고 loadUserByUsername("qwer")를 호출해요.
 * 우리는 DB에서 찾아서 PrincipalDetails로 포장해 돌려주기만 하면 돼요.
 * 비밀번호 비교는 Spring Security가 알아서 해요.
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberMapper.findByUsername(username);

        // 회원이 없거나, SNS로 가입한 회원이면(비밀번호가 없으니) 일반 로그인 불가
        if (member == null || !"local".equals(member.getProvider())) {
            throw new UsernameNotFoundException("아이디 또는 비밀번호가 틀렸어요.");
        }

        return new PrincipalDetails(member);
    }
}