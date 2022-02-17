package com.kbtg.db.dao;

import com.kbtg.db.bean.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, String> {

    Page<Product> findAll(Pageable pageReguest);

    Page<Product> findAllByNameContaining(String name, Pageable pageReguest);

}
