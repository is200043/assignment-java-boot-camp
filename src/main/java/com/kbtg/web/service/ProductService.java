package com.kbtg.web.service;

import com.kbtg.db.bean.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<Product> getProductList(Pageable pageable);

    Page<Product> searchProductListByName(String name, Pageable pageable);
}
