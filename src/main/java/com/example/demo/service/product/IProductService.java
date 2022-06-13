package com.example.demo.service.product;

import com.example.demo.model.Product;
import com.example.demo.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService extends IGeneralService<Product> {
    Page<Product> findProductByNameContaining(String name, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
    Iterable<Product> findProductPriceBetween(Double min, Double max);
    Page<Product> getProductWithName(Long id, Pageable pageable);
}
