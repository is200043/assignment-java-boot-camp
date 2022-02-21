package com.kbtg.web.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchancePayment {

    private String userPurchanceHistoryId;
    private String paymentType;
    private String cardNo;
    private String name;
    private String expireDate;
    private String ccv;

}
