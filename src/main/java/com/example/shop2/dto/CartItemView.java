package com.example.shop2.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 장바구니 화면에 보여줄 한 줄.
 * cart_item(수량) + product(이름, 가격, 사진)을 JOIN 한 결과를 담아요.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CartItemView {

    // cart_item 테이블에서
    private Long cartItemId;   // 장바구니 항목 번호 (수량 변경, 삭제할 때 사용)
    private Long productId;    // 상품 번호 (상품 상세로 가는 링크에 사용)
    private Integer quantity;  // 담은 수량

    // product 테이블에서
    private String brand;      // 브랜드
    private String name;       // 상품 이름
    private Integer price;     // 상품 한 개 가격
    private Integer stock;     // 현재 재고 (수량 최대값 제한에 사용)
    private String imageUrl;   // 사진

    /**
     * 이 줄의 합계 금액 = 가격 × 수량
     * 화면에서 ${item.totalPrice} 로 쓸 수 있어요.
     * 냉장고 여러 대처럼 금액이 커질 수 있어서 long으로 계산해요.
     */
    public long getTotalPrice() {
        return (long) price * quantity;
    }
}