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

import static com.kbtg.web.common.CommonConstants.Payment.TYPE_CREDIT_CARD;
import static com.kbtg.web.common.CommonConstants.Test.*;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

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
        JsonResponse jr = new JsonResponse();
        try {
            String id = UUID.randomUUID().toString();

            if (selectItemList.size() <= 0) {
                throw new RuntimeException("Please select item in basket");
            }
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

            jr.put("id", id);
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

    //    confirm credit card + promotion + shipping => สรุปจำนวนเงิน
    @PostMapping(value = "/payment/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> paymentTransaction(@PathVariable String userId, @RequestBody PurchancePayment purchancePayment) {
        JsonResponse jr = new JsonResponse();
        try {
            boolean isPaymentPass = false;
            if (TYPE_CREDIT_CARD.equals(purchancePayment.getPaymentType())) {
                isPaymentPass = this.isPaymentCreditCardPass(purchancePayment);
            }
            if(isFalse(isPaymentPass)) {
                purchanceService.purchancePayment(userId, purchancePayment, isPaymentPass);
                throw new RuntimeException("Credit card reject");
            }

            purchanceService.purchancePayment(userId, purchancePayment, isPaymentPass);
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

    private boolean isPaymentCreditCardPass(PurchancePayment purchancePayment) {
        if (CREDIT_CARD_APPROVED.equals(purchancePayment.getCardNo()) && CREDIT_CARD_EXP_DATE.equals(purchancePayment.getExpireDate()) && CREDIT_CARD_CCV.equals(purchancePayment.getCcv())) {
            return true;
        } else {
            return false;
        }
    }
}
