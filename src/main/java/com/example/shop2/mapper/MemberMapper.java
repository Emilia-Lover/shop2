package com.example.shop2.mapper;

import com.example.shop2.domain.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis 매퍼 인터페이스.
 * 여기에는 메서드 "이름"만 적고, 실제 SQL은 resources/mapper/MemberMapper.xml에 있어요.
 * 메서드 이름과 XML의 id가 같으면 MyBatis가 자동으로 연결해 줘요.
 */
@Mapper
public interface MemberMapper {

    // 회원 저장
    void insert(Member member);

    // 아이디로 회원 한 명 찾기 (3단계 로그인에서 사용)
    Member findByUsername(String username);

    // 같은 아이디가 몇 개 있는지 세기 (중복 확인용)
    int countByUsername(String username);
}