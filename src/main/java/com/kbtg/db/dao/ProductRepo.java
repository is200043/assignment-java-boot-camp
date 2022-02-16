package com.kbtg.db.dao;

import com.kbtg.db.bean.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, String> {

}
