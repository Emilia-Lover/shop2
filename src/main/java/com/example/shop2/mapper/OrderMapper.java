package com.example.shop2.mapper;

import com.example.shop2.domain.Order;
import com.example.shop2.domain.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 주문 SQL을 실행하는 MyBatis 매퍼.
 * 실제 SQL은 resources/mapper/OrderMapper.xml에 있어요.
 */
@Mapper
public interface OrderMapper {

    // 주문 저장 (저장 후 order.getId()에 새 주문 번호가 들어가요)
    void insertOrder(Order order);

    // 주문 상품 한 줄 저장
    void insertOrderItem(OrderItem item);

    // 이 회원의 주문 목록 (최신순)
    List<Order> findByMemberId(Long memberId);

    // 주문 한 건 (본인 것만)
    Order findByIdAndMemberId(@Param("id") Long id,
                              @Param("memberId") Long memberId);

    // 주문 안의 상품들
    List<OrderItem> findItemsByOrderId(Long orderId);
}