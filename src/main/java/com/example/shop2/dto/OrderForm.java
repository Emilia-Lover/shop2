package com.example.shop2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문서 입력값.
 *
 * [무엇을 주문하는지] 아래 셋 중 하나로 들어와요.
 *  - 장바구니 선택 주문: cartItemIds = [3, 5]
 *  - 장바구니 전체 주문: all = true
 *  - 바로 구매:         productId = 1, quantity = 2
 *
 * [어디로 보낼지] receiverName, phone, address
 */
@Getter
@Setter
public class OrderForm {

    // ===== 무엇을 주문하는지 =====
    private List<Long> cartItemIds = new ArrayList<>();  // 장바구니 항목 번호들
    private boolean all;                                  // 장바구니 전체 주문
    private Long productId;                               // 바로 구매 상품 번호
    private Integer quantity;                             // 바로 구매 수량

    // ===== 배송 정보 =====
    @NotBlank(message = "받는 분 이름을 입력해 주세요.")
    @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
    private String receiverName;

    @NotBlank(message = "연락처를 입력해 주세요.")
    @Pattern(regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$", message = "휴대폰 번호 형식으로 입력해 주세요. 예) 010-1234-5678")
    private String phone;

    @NotBlank(message = "배송 주소를 입력해 주세요.")
    @Size(max = 200, message = "주소는 200자 이하로 입력해 주세요.")
    private String address;

    // 바로 구매인지 확인 (화면과 컨트롤러에서 사용)
    public boolean isDirect() {
        return productId != null;
    }
}