package com.example.shop2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * cart_item 테이블의 한 줄(장바구니에 담긴 상품 하나)을 담는 클래스.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CartItem {

    private Long id;                  // 장바구니 항목 번호
    private Long memberId;            // 회원 번호
    private Long productId;           // 상품 번호
    private Integer quantity;         // 수량
    private LocalDateTime createdAt;  // 담은 날짜
}