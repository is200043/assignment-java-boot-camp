package com.kbtg.web.controller;

import com.kbtg.db.bean.UserPurchanceHistory;
import com.kbtg.db.bean.UserPurchanceHistoryDetail;
import com.kbtg.db.bean.id.UserPurchanceHistoryDetailId;
import com.kbtg.web.common.bean.BasketItem;
import com.kbtg.web.common.bean.JsonResponse;
import com.kbtg.web.common.bean.PurchancePayment;
import com.kbtg.web.common.bean.ShippingInfo;
import com.kbtg.web.component.ValidateCommon;
import com.kbtg.web.service.PurchanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/purchance")
public class PurchanceCtrl {

    private final PurchanceService purchanceService;
    private final ValidateCommon validateCommon;

    public PurchanceCtrl(PurchanceService purchanceService, ValidateCommon validateCommon) {
        this.purchanceService = purchanceService;
        this.validateCommon = validateCommon;
    }

    @PostMapping(value = "/checkout/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkout(@PathVariable String userId, @RequestBody List<BasketItem> selectItemList) {
        String id = UUID.randomUUID().toString();

        List<UserPurchanceHistoryDetail> userPurchanceHistoryDetailList = selectItemList.stream().map(it -> UserPurchanceHistoryDetail.builder().userPurchanceHistoryDetailId(UserPurchanceHistoryDetailId.builder()
                        .userPurchanceHistoryId(id)
                        .productId(it.getProductId())
                        .build())
                .qty(it.getQty())
                .price(it.getPrice())
                .options(it.getOptions())
                .shippingPrice(it.getShippingPrice())
                .build()).collect(Collectors.toList());

        BigDecimal totalPrice = BigDecimal.valueOf(userPurchanceHistoryDetailList.stream().mapToDouble(it -> it.getPrice().doubleValue()).sum());
        BigDecimal totalShipping = BigDecimal.valueOf(userPurchanceHistoryDetailList.stream().mapToDouble(it -> it.getShippingPrice().doubleValue()).sum());

        UserPurchanceHistory userPurchanceHistory = UserPurchanceHistory.builder()
                .id(id)
                .userId(userId)
                .userPurchanceHistoryDetailList(userPurchanceHistoryDetailList)
                .totalPrice(totalPrice)
                .totalShipping(totalShipping)
                .totalDiscount(BigDecimal.valueOf(0.00))
                .totalAmount(totalPrice.add(totalShipping))
                .build();

        purchanceService.insertUserPurchanceHistory(userPurchanceHistory);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body("ok");
    }


    @PostMapping(value = "/shipping/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkShipping(@PathVariable String userId, @RequestBody ShippingInfo shippingInfo) {
        JsonResponse jr = new JsonResponse();
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<ShippingInfo>> violations = validator.validate(shippingInfo);
            for (ConstraintViolation<ShippingInfo> violation : violations) {
                throw new RuntimeException(violation.getMessage());
            }
            validateCommon.checkFormatMobileNo(shippingInfo.getMobileNo()).orElseThrow(() -> new RuntimeException("Mobile no should be valid"));

            purchanceService.insertShippingInfo(userId, shippingInfo);

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .body("ok");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            jr.putErrorInternalServer(ex.getMessage());
            return ResponseEntity.internalServerError()
                    .body(jr);
        }
    }

    @PostMapping(value = "/payment/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> paymentTransaction(@PathVariable String userId, @RequestBody PurchancePayment purchancePayment) {
        JsonResponse jr = new JsonResponse();
        try {
            purchanceService.paymentPurchance(userId, purchancePayment);
            jr.putSuccess();

            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .body(jr);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            jr.putErrorInternalServer(ex.getMessage());
            return ResponseEntity.internalServerError()
                    .body(jr);
        }
    }

//    checkShipping => สรุปจำนวนเงิน
//    confirm credit card + promotion + shipping => สรุปจำนวนเงิน
}
