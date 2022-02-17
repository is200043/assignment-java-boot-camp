package com.kbtg.web.service.impl;

import com.kbtg.db.bean.Product;
import com.kbtg.db.dao.ProductRepo;
import com.kbtg.web.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;

    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public Page<Product> getProductList(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    @Override
    public Page<Product> searchProductListByName(String name, Pageable pageable) {
        return productRepo.findAllByNameContaining(name, pageable);
    }
}
