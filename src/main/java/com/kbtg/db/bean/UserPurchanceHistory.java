package com.kbtg.db.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    private String paymentResponse;
    private Date paymentAt;
    private String status;
    private String shippingName;
    private String shippingAddress;
    private String shippingDistrict;
    private String shippingProvince;
    private String shippingZipcode;
    private String shippingMobileNo;
    private String shippingEmail;

    @Transient
    private List<UserPurchanceHistoryDetail> userPurchanceHistoryDetailList;

}
