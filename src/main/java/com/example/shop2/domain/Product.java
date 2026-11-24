package com.example.shop2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * product 테이블의 한 줄(상품 한 개)을 담는 클래스.
 * image_url 컬럼 → imageUrl 변수처럼 자동으로 연결돼요.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Product {

    private Long id;                  // 상품 번호
    private String brand;             // 브랜드
    private String name;              // 상품 이름
    private Integer price;            // 가격
    private Integer stock;            // 재고 수량
    private String description;       // 상품 설명
    private String imageUrl;          // 이미지 주소
    private LocalDateTime createdAt;  // 등록일

    /**
     * 품절인지 확인 (재고가 0 이하면 품절)
     * 화면에서 ${product.soldOut} 으로 쓸 수 있어요. (is로 시작하면 Thymeleaf가 알아서 찾아줘요)
     */
    public boolean isSoldOut() {
        return stock == null || stock <= 0;
    }
}