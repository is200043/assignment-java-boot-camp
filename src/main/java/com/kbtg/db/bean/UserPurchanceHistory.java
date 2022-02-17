package com.kbtg.db.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPurchanceHistory {

    @Id
    private String id;
    private String userId;
    private BigDecimal totalPrice;
    private BigDecimal totalShipping;
    private BigDecimal totalDiscount;
    private BigDecimal totalAmount;
    private String paymentType;
    private String paymentMarking;

}
