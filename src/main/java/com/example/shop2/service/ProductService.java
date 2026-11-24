package com.example.shop2.service;

import com.example.shop2.domain.Product;
import com.example.shop2.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 상품 관련 규칙을 처리하는 곳
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    // 상품 전체 목록
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    /**
     * 상품 한 개 찾기.
     * /products/999 처럼 없는 번호로 들어오면 "404 Not Found" 오류 화면을 보여줘요.
     */
    public Product findById(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없어요. 번호: " + id);
        }
        return product;
    }
}