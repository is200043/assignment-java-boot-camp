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

import java.util.List;

@Slf4j
@Service
public class PurchanceServiceImpl implements PurchanceService {

    private static final String STATUS_CHECKOUT = "Checkout";
    private static final String STATUS_SHIPPING_INFO = "ShippingInfo";

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
        // remove item in basket
        userPurchanceHistoryDetailList.forEach(it -> userItemRepo.delete(UserItem.builder().userItemId(UserItemId.builder()
                        .userId(userPurchanceHistory.getUserId())
                        .productId(it.getUserPurchanceHistoryDetailId().getProductId())
                        .build())
                .build()));
        userPurchanceHistory.setStatus(STATUS_CHECKOUT);
        userPurchanceHistoryRepo.save(userPurchanceHistory);

        //ตัด Stock
        for (UserPurchanceHistoryDetail it : userPurchanceHistoryDetailList) {
            Product product = productRepo.findById(it.getUserPurchanceHistoryDetailId().getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            int qty = product.getQty() - it.getQty();
            if (qty < 0) {
                throw new RuntimeException("Product sold out");
            }
            product.setQty(qty);
            if (product.getPrice() != it.getPrice()) {
                throw new RuntimeException("Product change price");
            }
            productRepo.save(product);
        }

    }

    @Override
    public void insertShippingInfo(String userId, ShippingInfo shippingInfo) throws RuntimeException {
        UserPurchanceHistory userPurchanceHistory = userPurchanceHistoryRepo.findById(shippingInfo.getUserPurchanceHistoryId()).orElseThrow(() -> new RuntimeException("User Purchance History not found"));
        userPurchanceHistory.setStatus(STATUS_SHIPPING_INFO);
        userPurchanceHistory.setShippingName(shippingInfo.getName());
        userPurchanceHistory.setShippingAddress(shippingInfo.getAddress());
        userPurchanceHistory.setShippingDistrict(shippingInfo.getDistrict());
        userPurchanceHistory.setShippingProvince(shippingInfo.getProvince());
        userPurchanceHistory.setShippingZipcode(shippingInfo.getZipcode());
        userPurchanceHistory.setShippingMobileNo(shippingInfo.getMobileNo());
        userPurchanceHistory.setShippingEmail(shippingInfo.getEmail());
    }

    @Override
    public int paymentPurchance(String userId, PurchancePayment purchancePayment) throws RuntimeException {
        return 0;
    }
}
