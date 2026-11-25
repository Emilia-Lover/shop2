package com.example.shop2.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * orders 테이블의 한 줄 (주문 한 건).
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Order {

    private Long id;                  // 주문 번호
    private Long memberId;            // 주문한 회원
    private Long totalPrice;          // 총 결제 금액
    private String status;            // 주문 상태 (ORDERED)
    private String receiverName;      // 받는 분
    private String phone;             // 연락처
    private String address;           // 배송 주소
    private LocalDateTime createdAt;  // 주문일

    // ===== 주문 내역 목록 화면용 (SQL에서 계산해서 채워줘요) =====
    private String firstItemName;     // 첫 번째 상품 이름
    private Integer itemCount;        // 상품 종류 수

    // ===== 주문 상세 화면용 =====
    private List<OrderItem> items = new ArrayList<>();

    /**
     * 화면에 보여줄 상태 이름. ${order.statusName}
     */
    public String getStatusName() {
        if ("ORDERED".equals(status)) {
            return "주문완료";
        }
        return status;
    }

    /**
     * 주문 내역 목록에 보여줄 이름. 예) "55인치 4K UHD 스마트 TV 외 2건"
     */
    public String getSummaryName() {
        if (itemCount == null || itemCount <= 1) {
            return firstItemName;
        }
        return firstItemName + " 외 " + (itemCount - 1) + "건";
    }
}