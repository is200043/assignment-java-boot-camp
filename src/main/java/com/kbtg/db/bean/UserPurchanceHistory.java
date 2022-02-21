package com.kbtg.db.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentAt;
    private String status;
    private String shippingName;
    private String shippingAddress;
    private String shippingDistrict;
    private String shippingProvince;
    private String shippingZipcode;
    private String shippingMobileNo;
    private String shippingEmail;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;

    @Transient
    private List<UserPurchanceHistoryDetail> userPurchanceHistoryDetailList;

}
