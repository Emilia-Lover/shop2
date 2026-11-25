package com.example.shop2.controller;

import com.example.shop2.dto.CartItemView;
import com.example.shop2.dto.OrderForm;
import com.example.shop2.security.PrincipalDetails;
import com.example.shop2.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문 요청 처리.
 * /order, /orders 로 시작하는 주소는 로그인한 사람만 들어올 수 있어요.
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문서 화면 (GET /order/checkout)
     *  - 장바구니 선택: /order/checkout?cartItemIds=3&cartItemIds=5
     *  - 장바구니 전체: /order/checkout?all=true
     *  - 바로 구매:     /order/checkout?productId=1&quantity=2
     * 주소의 값들이 OrderForm에 자동으로 담겨요.
     */
    @GetMapping("/order/checkout")
    public String checkout(@AuthenticationPrincipal PrincipalDetails principal,
                           @ModelAttribute("orderForm") OrderForm form,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Long memberId = principal.getMember().getId();

        List<CartItemView> lines;
        try {
            lines = orderService.getCheckoutLines(memberId, form);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return backTo(form);
        }

        // 장바구니 주문이면, 지금 화면에 보이는 상품 번호들을 그대로 기억해요.
        // ("전체 주문"도 번호 목록으로 바꿔 두면, 결제 직전에 다른 탭에서 담은 상품이 섞이지 않아요)
        if (!form.isDirect()) {
            List<Long> ids = new ArrayList<>();
            for (CartItemView line : lines) {
                ids.add(line.getCartItemId());
            }
            form.setCartItemIds(ids);
            form.setAll(false);
        }

        // 받는 분 이름은 회원 이름으로 미리 채워줘요
        if (form.getReceiverName() == null) {
            form.setReceiverName(principal.getMember().getName());
        }

        addCheckoutModel(model, lines);
        return "order/checkout";
    }

    /**
     * 결제하기 = 주문 저장 (POST /order)
     */
    @PostMapping("/order")
    public String placeOrder(@AuthenticationPrincipal PrincipalDetails principal,
                             @Valid @ModelAttribute("orderForm") OrderForm form,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Long memberId = principal.getMember().getId();

        // 1. 배송 정보에 문제가 있으면 주문서를 다시 보여줘요 (오류 문구와 함께)
        if (bindingResult.hasErrors()) {
            try {
                addCheckoutModel(model, orderService.getCheckoutLines(memberId, form));
                return "order/checkout";
            } catch (IllegalStateException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return backTo(form);
            }
        }

        // 2. 주문 저장
        try {
            Long orderId = orderService.placeOrder(memberId, form);
            redirectAttributes.addFlashAttribute("message", "주문이 완료됐어요! 주문번호는 " + orderId + "번이에요.");
            return "redirect:/orders/" + orderId;
        } catch (IllegalStateException e) {
            // 재고 부족 등 → 원래 있던 곳(장바구니 또는 상품 상세)으로
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return backTo(form);
        }
    }

    /**
     * 주문 내역 (GET /orders)
     */
    @GetMapping("/orders")
    public String list(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        model.addAttribute("orders", orderService.getOrders(principal.getMember().getId()));
        return "order/list";
    }

    /**
     * 주문 상세 (GET /orders/3)
     */
    @GetMapping("/orders/{orderId}")
    public String detail(@AuthenticationPrincipal PrincipalDetails principal,
                         @PathVariable Long orderId,
                         Model model) {
        model.addAttribute("order", orderService.getOrder(principal.getMember().getId(), orderId));
        return "order/detail";
    }

    // 주문서 화면에 필요한 값 담기
    private void addCheckoutModel(Model model, List<CartItemView> lines) {
        model.addAttribute("lines", lines);
        model.addAttribute("totalPrice", orderService.getTotalPrice(lines));
    }

    // 문제가 생겼을 때 돌아갈 곳: 바로 구매면 상품 상세, 아니면 장바구니
    private String backTo(OrderForm form) {
        if (form.isDirect()) {
            return "redirect:/products/" + form.getProductId();
        }
        return "redirect:/cart";
    }
}