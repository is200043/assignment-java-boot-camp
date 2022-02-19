package com.kbtg.web.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasketItem {

    private String productId;
    private BigDecimal price;
    private Integer qty;
    private String options;

}
