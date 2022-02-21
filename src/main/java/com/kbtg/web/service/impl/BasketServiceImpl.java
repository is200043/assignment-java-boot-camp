package com.kbtg.web.service.impl;

import com.kbtg.db.bean.Product;
import com.kbtg.db.bean.UserItem;
import com.kbtg.db.bean.id.UserItemId;
import com.kbtg.db.dao.ProductRepo;
import com.kbtg.db.dao.UserItemRepo;
import com.kbtg.web.common.bean.BasketItem;
import com.kbtg.web.service.BasketService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BasketServiceImpl implements BasketService {

    private final UserItemRepo userItemRepo;
    private final ProductRepo productRepo;

    public BasketServiceImpl(UserItemRepo userItemRepo, ProductRepo productRepo) {
        this.userItemRepo = userItemRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<BasketItem> getBasketItemListByUserId(String userId) {
        return userItemRepo.findByUserItemId_UserIdEquals(userId).stream().map(it -> BasketItem.builder()
                        .productId(it.getUserItemId().getProductId())
                        .price(it.getPrice())
                        .qty(it.getQty())
                        .options(it.getOptions())
                        .shippingPrice(it.getShippingPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void upsertUserItemByUserId(String userId, List<BasketItem> basketItemList) {
        List<Product> productList = productRepo.findAll();
        List<UserItem> oUserItemList = userItemRepo.findByUserItemId_UserIdEquals(userId);
        //deleted qty <= 0
        oUserItemList.forEach(it -> {
            if (it.getQty() <= 0) {
                userItemRepo.delete(it);
            }
        });
        oUserItemList = oUserItemList.stream().filter(it -> it.getQty() > 0).collect(Collectors.toList());

        List<UserItem> finalOldUserItemList = oUserItemList;
        basketItemList.forEach(it -> {
            Product product = productList.stream().filter(p -> p.getId().equals(it.getProductId())).findFirst().orElseThrow(() -> new RuntimeException("Product not found"));
            UserItem nUserItem = UserItem.builder().userItemId(UserItemId.builder().userId(userId).productId(it.getProductId()).build())
                    .price(product.getPrice()).qty(it.getQty()).options(it.getOptions()).shippingPrice(it.getShippingPrice() == null ? product.getShippingPrice() : it.getShippingPrice())
                    .build();

            Optional<UserItem> oUserItemOpt = finalOldUserItemList.stream().filter(oItem -> it.getProductId().equals(oItem.getUserItemId().getProductId())).findFirst();
            if (oUserItemOpt.isPresent()) {
                UserItem oUserItem = oUserItemOpt.get();
                nUserItem.setQty(oUserItem.getQty() + nUserItem.getQty());
            }
            if (nUserItem.getQty() < 0) {
                throw new RuntimeException("Product " + it.getProductId() + " < 0");
            }
            userItemRepo.save(nUserItem);
        });

        List<UserItem> currentUserItemList = userItemRepo.findByUserItemId_UserIdEquals(userId);
        log.info("current basket: {}", new JSONArray(currentUserItemList));
    }
}
