package com.example.shop2.service;

import com.example.shop2.domain.CartItem;
import com.example.shop2.domain.Product;
import com.example.shop2.dto.CartItemView;
import com.example.shop2.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 장바구니 규칙을 처리하는 곳.
 * 문제가 있으면 IllegalStateException에 안내 문구를 담아서 던지고,
 * 컨트롤러가 그 문구를 화면에 보여줘요.
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final ProductService productService;

    /**
     * 장바구니에 담기
     */
    @Transactional
    public void addToCart(Long memberId, Long productId, int quantity) {
        // 1. 수량 확인
        if (quantity < 1) {
            throw new IllegalStateException("수량은 1개 이상 선택해 주세요.");
        }

        // 2. 상품 확인 (없는 상품이면 ProductService가 404를 던져요)
        Product product = productService.findById(productId);

        // 3. 품절 확인
        if (product.isSoldOut()) {
            throw new IllegalStateException("품절된 상품이라 담을 수 없어요.");
        }

        // 4. 재고 확인: (이미 담은 수량 + 이번에 담을 수량) 이 재고보다 많으면 안 돼요
        Integer already = cartMapper.findQuantity(memberId, productId);
        int alreadyQuantity = (already == null) ? 0 : already;

        if (alreadyQuantity + quantity > product.getStock()) {
            throw new IllegalStateException(
                    "재고가 부족해요. (남은 재고 " + product.getStock() + "개, 장바구니에 이미 " + alreadyQuantity + "개)");
        }

        // 5. 담기 (이미 있으면 수량이 더해져요)
        CartItem cartItem = new CartItem();
        cartItem.setMemberId(memberId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartMapper.insertOrAddQuantity(cartItem);
    }

    /**
     * 장바구니 목록
     */
    public List<CartItemView> getCartItems(Long memberId) {
        return cartMapper.findByMemberId(memberId);
    }

    /**
     * 장바구니 총 금액 = 각 줄의 (가격 × 수량)을 모두 더한 값
     */
    public long getTotalPrice(List<CartItemView> items) {
        return items.stream()
                .mapToLong(CartItemView::getTotalPrice)
                .sum();
    }

    /**
     * 수량 바꾸기
     */
    @Transactional
    public void changeQuantity(Long memberId, Long cartItemId, int quantity) {
        // 본인 장바구니에 있는 항목인지 확인
        CartItemView item = cartMapper.findByIdAndMemberId(cartItemId, memberId);
        if (item == null) {
            throw new IllegalStateException("장바구니에 없는 상품이에요.");
        }
        if (quantity < 1) {
            throw new IllegalStateException("수량은 1개 이상이어야 해요. 빼려면 [삭제]를 눌러 주세요.");
        }
        if (quantity > item.getStock()) {
            throw new IllegalStateException(
                    item.getName() + "은(는) 최대 " + item.getStock() + "개까지 담을 수 있어요.");
        }
        cartMapper.updateQuantity(cartItemId, memberId, quantity);
    }

    /**
     * 장바구니에서 빼기
     */
    @Transactional
    public void remove(Long memberId, Long cartItemId) {
        int deleted = cartMapper.delete(cartItemId, memberId);
        if (deleted == 0) {
            throw new IllegalStateException("장바구니에 없는 상품이에요.");
        }
    }
}