package com.example.shop2.controller;

import com.example.shop2.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 화면 담당 - 메인 화면에 상품 목록을 보여줘요
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    // http://localhost:8080/ 으로 들어오면 상품 목록을 담아서 templates/index.html을 보여줘요
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        return "index";
    }
}