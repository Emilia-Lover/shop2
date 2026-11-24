package com.example.shop2.service;

import com.example.shop2.domain.Member;
import com.example.shop2.dto.MemberJoinForm;
import com.example.shop2.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관련 규칙을 처리하는 곳.
 * @RequiredArgsConstructor → final 변수들을 Spring이 자동으로 넣어줘요 (생성자 주입).
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * @return 새로 만들어진 회원 번호
     */
    @Transactional
    public Long join(MemberJoinForm form) {
        // 1. 아이디 중복 확인
        if (memberMapper.countByUsername(form.getUsername()) > 0) {
            throw new IllegalStateException("이미 사용 중인 아이디예요.");
        }

        // 2. 입력값(신청서)을 회원 카드(Member)로 옮겨 담기
        Member member = new Member();
        member.setUsername(form.getUsername());
        member.setPassword(passwordEncoder.encode(form.getPassword())); // 비밀번호 암호화!
        member.setName(form.getName());
        member.setEmail(form.getEmail());
        member.setRole("ROLE_USER");
        member.setProvider("local"); // 일반 회원가입

        // 3. DB에 저장
        memberMapper.insert(member);

        // insert 후에는 useGeneratedKeys 덕분에 id가 채워져 있어요
        return member.getId();
    }
}