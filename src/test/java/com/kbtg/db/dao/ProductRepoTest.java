package com.kbtg.db.dao;

import com.kbtg.db.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class ProductRepoTest {

    @Autowired
    ProductRepo productRepo;

    @Test
    public void save() {
        productRepo.save(Product.builder()
                .id(UUID.randomUUID().toString())
                .name("KF94 3D Maskแมสทรงสวย เเพ๊คละ10 ชิ้น แมสป้องกันฝุ่น เชื้อโรค ทรงเกาหลี แพคเกจใหม่")
                .price(BigDecimal.valueOf(10.00))
                .build());
        log.info("product list: {}", productRepo.findAll());
    }
}
