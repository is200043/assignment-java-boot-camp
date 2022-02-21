package com.kbtg.web.service.impl;

import com.kbtg.db.bean.Product;
import com.kbtg.db.bean.UserItem;
import com.kbtg.db.bean.UserPurchanceHistory;
import com.kbtg.db.bean.UserPurchanceHistoryDetail;
import com.kbtg.db.bean.id.UserItemId;
import com.kbtg.db.dao.ProductRepo;
import com.kbtg.db.dao.UserItemRepo;
import com.kbtg.db.dao.UserPurchanceHistoryRepo;
import com.kbtg.web.common.bean.PurchancePayment;
import com.kbtg.web.common.bean.ShippingInfo;
import com.kbtg.web.service.PurchanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import static com.kbtg.web.common.CommonConstants.UserPurchanceHistory.*;

@Slf4j
@Service
public class PurchanceServiceImpl implements PurchanceService {

    private final UserItemRepo userItemRepo;
    private final UserPurchanceHistoryRepo userPurchanceHistoryRepo;
    private final ProductRepo productRepo;

    public PurchanceServiceImpl(UserItemRepo userItemRepo, UserPurchanceHistoryRepo userPurchanceHistoryRepo, ProductRepo productRepo) {
        this.userItemRepo = userItemRepo;
        this.userPurchanceHistoryRepo = userPurchanceHistoryRepo;
        this.productRepo = productRepo;
    }

    @Override
    @Transactional
    public void insertUserPurchanceHistory(UserPurchanceHistory userPurchanceHistory) throws RuntimeException {
        List<UserPurchanceHistoryDetail> userPurchanceHistoryDetailList = userPurchanceHistory.getUserPurchanceHistoryDetailList();
        userPurchanceHistory.setStatus(STATUS_CHECKOUT);
        userPurchanceHistory.setCreatedAt(new Date());
        userPurchanceHistory.setModifiedAt(new Date());
        userPurchanceHistoryRepo.save(userPurchanceHistory);

        for (UserPurchanceHistoryDetail it : userPurchanceHistoryDetailList) {
            Product product = productRepo.findById(it.getUserPurchanceHistoryDetailId().getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            // remove item in basket
            userItemRepo.delete(UserItem.builder().userItemId(UserItemId.builder()
                            .userId(userPurchanceHistory.getUserId())
                            .productId(it.getUserPurchanceHistoryDetailId().getProductId())
                            .build())
                    .build());

            //ตัด Stock
            int qty = product.getQty() - it.getQty();
            if (qty < 0) {
                throw new RuntimeException("Product sold out");
            }
            product.setQty(qty);
            if (product.getPrice().doubleValue() != it.getPrice().doubleValue()) {
                throw new RuntimeException("Product change price");
            }
            productRepo.save(product);
        }

    }

    @Override
    public void insertShippingInfo(String userId, ShippingInfo shippingInfo) throws RuntimeException {
        UserPurchanceHistory userPurchanceHistory = userPurchanceHistoryRepo.findById(shippingInfo.getUserPurchanceHistoryId()).orElseThrow(() -> new RuntimeException("User Purchance History not found"));
        userPurchanceHistory.setModifiedAt(new Date());
        userPurchanceHistory.setStatus(STATUS_SHIPPING_INFO);
        userPurchanceHistory.setShippingName(shippingInfo.getName());
        userPurchanceHistory.setShippingAddress(shippingInfo.getAddress());
        userPurchanceHistory.setShippingDistrict(shippingInfo.getDistrict());
        userPurchanceHistory.setShippingProvince(shippingInfo.getProvince());
        userPurchanceHistory.setShippingZipcode(shippingInfo.getZipcode());
        userPurchanceHistory.setShippingMobileNo(shippingInfo.getMobileNo());
        userPurchanceHistory.setShippingEmail(shippingInfo.getEmail());
        userPurchanceHistoryRepo.save(userPurchanceHistory);
    }

    @Override
    public void purchancePayment(String userId, PurchancePayment purchancePayment, boolean isPaymentPass) throws RuntimeException {
        UserPurchanceHistory userPurchanceHistory = userPurchanceHistoryRepo.findById(purchancePayment.getUserPurchanceHistoryId()).orElseThrow(() -> new RuntimeException("User Purchance History not found"));
        userPurchanceHistory.setModifiedAt(new Date());
        if (!STATUS_SHIPPING_INFO.equals(userPurchanceHistory.getStatus())) {
            throw new RuntimeException("UserPurchanceHistory status failed");
        }
        if (!userId.equals(userPurchanceHistory.getUserId())) {
            throw new RuntimeException("User not match");
        }
        Date expiredAt = Date.from(userPurchanceHistory.getCreatedAt().toInstant().plus(Duration.ofMinutes(15)));
        if (expiredAt.before(new Date())) {
            userPurchanceHistory.setStatus(STATUS_EXPIRED);
            userPurchanceHistoryRepo.save(userPurchanceHistory);
            throw new RuntimeException("Payment expired");
        }
        userPurchanceHistory.setStatus(isPaymentPass ? STATUS_PAID : STATUS_REJECT);
        userPurchanceHistoryRepo.save(userPurchanceHistory);
    }
}
