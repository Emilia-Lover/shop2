package com.example.shop2.controller;

import com.example.shop2.dto.CartItemView;
import com.example.shop2.security.PrincipalDetails;
import com.example.shop2.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 장바구니 요청 처리.
 * /cart 로 시작하는 주소는 SecurityConfig의 anyRequest().authenticated() 때문에
 * 로그인한 사람만 들어올 수 있어요.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 보기 (GET /cart)
     */
    @GetMapping
    public String list(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        Long memberId = principal.getMember().getId();

        List<CartItemView> items = cartService.getCartItems(memberId);
        model.addAttribute("items", items);
        model.addAttribute("totalPrice", cartService.getTotalPrice(items));
        return "cart/list";
    }

    /**
     * 장바구니 담기 (POST /cart/add)
     * 상품 상세 화면의 form에서 productId, quantity를 보내줘요.
     */
    @PostMapping("/add")
    public String add(@AuthenticationPrincipal PrincipalDetails principal,
                      @RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(principal.getMember().getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("message", "장바구니에 " + quantity + "개 담았어요!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        // 담은 뒤에는 보고 있던 상품 상세 화면으로 돌아가요
        return "redirect:/products/" + productId;
    }

    /**
     * 수량 바꾸기 (POST /cart/5/quantity)
     */
    @PostMapping("/{cartItemId}/quantity")
    public String changeQuantity(@AuthenticationPrincipal PrincipalDetails principal,
                                 @PathVariable Long cartItemId,
                                 @RequestParam int quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.changeQuantity(principal.getMember().getId(), cartItemId, quantity);
            redirectAttributes.addFlashAttribute("message", "수량을 바꿨어요.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * 장바구니에서 빼기 (POST /cart/5/delete)
     */
    @PostMapping("/{cartItemId}/delete")
    public String delete(@AuthenticationPrincipal PrincipalDetails principal,
                         @PathVariable Long cartItemId,
                         RedirectAttributes redirectAttributes) {
        try {
            cartService.remove(principal.getMember().getId(), cartItemId);
            redirectAttributes.addFlashAttribute("message", "장바구니에서 뺐어요.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * 로그인 안 한 사람이 [로그인하고 장바구니 담기]를 눌렀을 때 쓰는 주소 (GET /cart/login-then/3)
     *
     * 이 주소도 /cart 로 시작해서 로그인이 필요해요.
     * → Spring Security가 "원래 가려던 주소"로 기억하고 로그인 화면으로 보내요.
     * → 로그인 성공하면 이 주소로 돌아와요.
     * → 여기서 다시 상품 상세 화면으로 보내줘요.
     * 결과적으로 "로그인하고 나서 보던 상품으로 돌아오기"가 돼요.
     */
    @GetMapping("/login-then/{productId}")
    public String loginThenBackToProduct(@PathVariable Long productId) {
        return "redirect:/products/" + productId;
    }
}