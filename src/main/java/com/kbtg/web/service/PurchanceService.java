package com.kbtg.web.service;

import com.kbtg.db.bean.UserPurchanceHistory;
import com.kbtg.web.common.bean.PurchancePayment;
import com.kbtg.web.common.bean.ShippingInfo;

public interface PurchanceService {

    void insertUserPurchanceHistory(UserPurchanceHistory userPurchanceHistory) throws RuntimeException;

    void insertShippingInfo(String userId, ShippingInfo shippingInfo) throws RuntimeException;

    void purchancePayment(String userId, PurchancePayment purchancePayment, boolean isPaymentPass) throws RuntimeException;

    void adjustStock(UserPurchanceHistory userPurchanceHistory, String status) throws RuntimeException;

}
