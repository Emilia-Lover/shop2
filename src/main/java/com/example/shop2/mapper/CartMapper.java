package com.example.shop2.mapper;

import com.example.shop2.domain.CartItem;
import com.example.shop2.dto.CartItemView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 장바구니 SQL을 실행하는 MyBatis 매퍼.
 * 실제 SQL은 resources/mapper/CartMapper.xml에 있어요.
 *
 * @Param("memberId") → 값이 2개 이상일 때, XML에서 #{memberId} 처럼 이름으로 꺼낼 수 있게 붙여줘요.
 */
@Mapper
public interface CartMapper {

    // 장바구니에 담기 (이미 담긴 상품이면 수량을 더해요)
    void insertOrAddQuantity(CartItem cartItem);

    // 이 회원이 이 상품을 이미 몇 개 담았는지 (안 담았으면 null)
    Integer findQuantity(@Param("memberId") Long memberId,
                         @Param("productId") Long productId);

    // 이 회원의 장바구니 전체 (상품 정보 포함)
    List<CartItemView> findByMemberId(Long memberId);

    // 장바구니 한 줄 찾기 (본인 것만)
    CartItemView findByIdAndMemberId(@Param("id") Long id,
                                     @Param("memberId") Long memberId);

    // 수량 바꾸기 (본인 것만)
    int updateQuantity(@Param("id") Long id,
                       @Param("memberId") Long memberId,
                       @Param("quantity") int quantity);

    // 장바구니에서 빼기 (본인 것만)
    int delete(@Param("id") Long id,
               @Param("memberId") Long memberId);
}