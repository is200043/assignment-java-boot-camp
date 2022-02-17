package com.kbtg.db.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    private String name;
    private BigDecimal price;
    private BigDecimal priceBeforeDiscount;
    private Integer qty;
    private String brand;
    private String options;
    private String pics;
    private BigDecimal rating;
    private Integer reviews;
    private String productGroup;
    private String merchantId;
    private Date endPromotionAt;

}
