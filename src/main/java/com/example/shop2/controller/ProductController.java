package com.example.shop2.controller;

import com.example.shop2.domain.Product;
import com.example.shop2.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 상품 화면 요청 처리
 */
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 상세 화면 (GET /products/3)
     * @PathVariable → 주소 안의 {id} 자리에 있는 숫자(3)를 꺼내줘요
     */
    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product/detail";
    }
}