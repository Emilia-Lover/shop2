package com.example.shop2.mapper;

import com.example.shop2.domain.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 상품 관련 SQL을 실행하는 MyBatis 매퍼.
 * 실제 SQL은 resources/mapper/ProductMapper.xml에 있어요.
 */
@Mapper
public interface ProductMapper {

    // 상품 전체 목록 (최근 등록순)
    List<Product> findAll();

    // 상품 번호로 한 개 찾기 (없으면 null)
    Product findById(Long id);
}