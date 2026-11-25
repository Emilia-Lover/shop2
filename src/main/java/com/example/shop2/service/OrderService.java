package com.example.shop2.service;

import com.example.shop2.domain.Order;
import com.example.shop2.domain.OrderItem;
import com.example.shop2.domain.Product;
import com.example.shop2.dto.CartItemView;
import com.example.shop2.dto.OrderForm;
import com.example.shop2.mapper.CartMapper;
import com.example.shop2.mapper.OrderMapper;
import com.example.shop2.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

/**
 * 주문 규칙을 처리하는 곳.
 * 문제가 있으면 IllegalStateException에 안내 문구를 담아서 던져요.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;

    /**
     * 주문서에 보여줄 상품 목록 만들기.
     * 장바구니 주문이든 바로 구매든, 화면에서는 똑같이 CartItemView 목록으로 다뤄요.
     * (바로 구매는 장바구니 번호(cartItemId)만 비어 있어요)
     */
    public List<CartItemView> getCheckoutLines(Long memberId, OrderForm form) {
        List<CartItemView> lines;

        if (form.isDirect()) {
            // ① 바로 구매: 상품 하나
            lines = List.of(toDirectLine(form.getProductId(), form.getQuantity()));
        } else if (form.isAll()) {
            // ② 장바구니 전체
            lines = cartMapper.findByMemberId(memberId);
        } else if (form.getCartItemIds() != null && !form.getCartItemIds().isEmpty()) {
            // ③ 장바구니에서 선택한 것만 (본인 장바구니 것만 찾아져요)
            lines = cartMapper.findByIdsAndMemberId(form.getCartItemIds(), memberId);
        } else {
            throw new IllegalStateException("주문할 상품을 선택해 주세요.");
        }

        if (lines.isEmpty()) {
            throw new IllegalStateException("주문할 상품이 없어요. 장바구니를 확인해 주세요.");
        }

        // 품절, 재고 초과 확인
        for (CartItemView line : lines) {
            if (line.getStock() <= 0) {
                throw new IllegalStateException(line.getName() + "은(는) 품절이에요. 장바구니에서 빼 주세요.");
            }
            if (line.getQuantity() > line.getStock()) {
                throw new IllegalStateException(
                        line.getName() + "은(는) 최대 " + line.getStock() + "개까지 주문할 수 있어요.");
            }
        }
        return lines;
    }

    /**
     * 총 결제 금액 = 각 줄의 (가격 × 수량)을 모두 더한 값
     */
    public long getTotalPrice(List<CartItemView> lines) {
        return lines.stream()
                .mapToLong(CartItemView::getTotalPrice)
                .sum();
    }

    /**
     * 주문하기 (결제하기 버튼).
     *
     * @Transactional → 아래 과정 중 하나라도 실패하면 전부 없던 일로 되돌려요.
     * 예) TV는 재고가 줄었는데 냉장고가 재고 부족이면, TV 재고도 원래대로 돌아가요.
     *
     * @return 새로 만들어진 주문 번호
     */
    @Transactional
    public Long placeOrder(Long memberId, OrderForm form) {
        // 1. 주문할 상품 다시 확인 (화면에서 보낸 가격은 믿지 않고, DB에서 다시 꺼내요)
        List<CartItemView> lines = getCheckoutLines(memberId, form);

        // 2. 재고 줄이기 (재고가 모자라면 0이 돌아와요)
        for (CartItemView line : lines) {
            int updated = productMapper.decreaseStock(line.getProductId(), line.getQuantity());
            if (updated == 0) {
                throw new IllegalStateException(line.getName() + "의 재고가 부족해서 주문하지 못했어요.");
            }
        }

        // 3. 주문(orders) 저장
        Order order = new Order();
        order.setMemberId(memberId);
        order.setTotalPrice(getTotalPrice(lines));
        order.setStatus("ORDERED");
        order.setReceiverName(form.getReceiverName());
        order.setPhone(form.getPhone());
        order.setAddress(form.getAddress());
        orderMapper.insertOrder(order); // 저장 후 order.getId()에 주문 번호가 들어가요

        // 4. 주문 상품(order_item) 저장 - 주문 당시 이름과 가격을 복사해 둬요
        for (CartItemView line : lines) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(line.getProductId());
            item.setBrand(line.getBrand());
            item.setProductName(line.getName());
            item.setPrice(line.getPrice());
            item.setQuantity(line.getQuantity());
            orderMapper.insertOrderItem(item);
        }

        // 5. 장바구니에서 주문한 상품 빼기 (바로 구매는 장바구니 번호가 없어서 건너뛰어요)
        List<Long> orderedCartItemIds = lines.stream()
                .map(CartItemView::getCartItemId)
                .filter(Objects::nonNull)
                .toList();
        if (!orderedCartItemIds.isEmpty()) {
            cartMapper.deleteByIds(orderedCartItemIds, memberId);
        }

        return order.getId();
    }

    /**
     * 내 주문 목록
     */
    public List<Order> getOrders(Long memberId) {
        return orderMapper.findByMemberId(memberId);
    }

    /**
     * 주문 한 건 (상품 목록 포함). 남의 주문이거나 없는 주문이면 404.
     */
    public Order getOrder(Long memberId, Long orderId) {
        Order order = orderMapper.findByIdAndMemberId(orderId, memberId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없어요. 번호: " + orderId);
        }
        order.setItems(orderMapper.findItemsByOrderId(orderId));
        return order;
    }

    /**
     * 바로 구매할 상품을 주문서 한 줄 모양(CartItemView)으로 만들기
     */
    private CartItemView toDirectLine(Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalStateException("수량은 1개 이상 선택해 주세요.");
        }
        Product product = productService.findById(productId); // 없으면 404

        CartItemView line = new CartItemView();
        line.setCartItemId(null); // 장바구니를 거치지 않았어요
        line.setProductId(product.getId());
        line.setBrand(product.getBrand());
        line.setName(product.getName());
        line.setPrice(product.getPrice());
        line.setStock(product.getStock());
        line.setImageUrl(product.getImageUrl());
        line.setQuantity(quantity);
        return line;
    }
}