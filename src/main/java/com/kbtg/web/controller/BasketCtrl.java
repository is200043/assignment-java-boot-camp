package com.kbtg.web.controller;

import com.kbtg.web.common.bean.BasketItem;
import com.kbtg.web.service.BasketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/basket")
public class BasketCtrl {

    private final BasketService basketService;

    public BasketCtrl(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBasketByUserId(@PathVariable String userId) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(basketService.getUserItemListByUserId(userId));
    }

    @PutMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upsertBasket(@PathVariable String userId, @RequestBody List<BasketItem> basketItemList) {
        basketService.upsertUserItemByUserId(userId, basketItemList);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body("ok");
    }
}
