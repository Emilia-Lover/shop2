package com.example.shop2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * order_item 테이블의 한 줄 (주문 안의 상품 하나).
 * 주문 당시의 브랜드, 이름, 가격을 그대로 복사해 두어요.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderItem {

    private Long id;             // 주문 상품 번호
    private Long orderId;        // 어느 주문인지
    private Long productId;      // 어떤 상품인지 (상품 상세 링크용)
    private String brand;        // 주문 당시 브랜드
    private String productName;  // 주문 당시 상품 이름
    private Integer price;       // 주문 당시 가격
    private Integer quantity;    // 수량

    // 이 줄의 합계 = 가격 × 수량
    public long getTotalPrice() {
        return (long) price * quantity;
    }
}