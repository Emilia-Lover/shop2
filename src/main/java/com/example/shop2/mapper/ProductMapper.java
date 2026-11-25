package com.example.shop2.mapper;

import com.example.shop2.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 상품 관련 SQL을 실행하는 MyBatis 매퍼.
 * 실제 SQL은 resources/mapper/ProductMapper.xml에 있어요.
 */
@Mapper
public interface ProductMapper {

    // 상품 전체 목록
    List<Product> findAll();

    // 상품 번호로 한 개 찾기 (없으면 null)
    Product findById(Long id);

    /**
     * 재고 줄이기.
     * 재고가 충분할 때만 줄어들어요.
     * @return 1 = 성공, 0 = 재고 부족이라 안 줄어듦
     */
    int decreaseStock(@Param("id") Long id,
                      @Param("quantity") int quantity);
}