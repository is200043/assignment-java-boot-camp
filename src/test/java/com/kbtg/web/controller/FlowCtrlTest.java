package com.kbtg.web.controller;

import com.kbtg.db.bean.Product;
import com.kbtg.db.bean.UserPurchanceHistory;
import com.kbtg.db.bean.UserPurchanceHistoryDetail;
import com.kbtg.db.bean.id.UserItemId;
import com.kbtg.db.dao.ProductRepo;
import com.kbtg.db.dao.UserItemRepo;
import com.kbtg.db.dao.UserPurchanceHistoryDetailRepo;
import com.kbtg.db.dao.UserPurchanceHistoryRepo;
import com.kbtg.web.common.bean.BasketItem;
import com.kbtg.web.common.bean.PurchancePayment;
import com.kbtg.web.common.bean.ShippingInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kbtg.web.common.CommonConstants.Payment.TYPE_CREDIT_CARD;
import static com.kbtg.web.common.CommonConstants.Test.*;
import static com.kbtg.web.common.CommonConstants.UserPurchanceHistory.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FlowCtrlTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private UserItemRepo userItemRepo;
    @Autowired
    private UserPurchanceHistoryRepo userPurchanceHistoryRepo;
    @Autowired
    private UserPurchanceHistoryDetailRepo userPurchanceHistoryDetailRepo;

    private MockMvc mockMvc;
    private String userPurchanceHistoryId;

    @BeforeAll
    public void beforeAll() throws Exception {
        System.setOut(new PrintStream(System.out, true, UTF_8.name()));
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(((request, response, chain) -> {
            response.setCharacterEncoding(UTF_8.name());
            chain.doFilter(request, response);
        })).build();
    }


    @Test
    @Order(1)
    @DisplayName("ค้นหาซัมซุงฮีโร่")
    public void flowSearchProduct() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/product/ซัมซุงฮีโร่"))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONObject contentJObj = new JSONObject(content);
        contentJObj.getJSONArray("content").toList().forEach(it -> {
            Map<String, Object> map = (HashMap<String, Object>) it;
            assertEquals(map.get("id"), PRODUCT_ID_SAMSUNG_HERO);
        });
    }

    @Test
    @Order(3)
    @DisplayName("ดูสินค้าในตะกร้า")
    public void flowGetBasket() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/basket/" + USER_ID_CHONLAKORN))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
    }

    @Test
    @Order(3)
    @DisplayName("เพิ่มสินค้าเข้าตะกร้า")
    public void flowAddProductToBasket() throws Exception {
        List<BasketItem> basketItemList = new ArrayList<>();
        basketItemList.add(BasketItem.builder() // เพิ่ม Item ไม่มี Options
                .productId(PRODUCT_ID_SAMSUNG_HERO)
                .price(BigDecimal.valueOf(599.00))
                .qty(1)
                .shippingPrice(BigDecimal.valueOf(40.00))
                .build());
        basketItemList.add(BasketItem.builder() // ลด Item
                .productId(PRODUCT_ID_IPHONE6)
                .qty(-1)
                .build());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/basket/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONArray(basketItemList).toString()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        userItemRepo.findByUserItemId_UserIdEquals(USER_ID_CHONLAKORN).forEach(it -> {
            UserItemId userItemId = it.getUserItemId();
            if (PRODUCT_ID_SAMSUNG_HERO.equals(userItemId.getProductId())) {
                assertEquals(it.getQty(), 1);
                assertEquals(it.getPrice().doubleValue(), 599.00);
            } else if (PRODUCT_ID_IPHONE6.equals(userItemId.getProductId())) {
                assertEquals(it.getQty(), 1);
            }
        });
    }

    @Test
    @Order(4)
    @DisplayName("checkout")
    public void flowCheckout() throws Exception {
        List<BasketItem> basketItemList = new ArrayList<>();
        basketItemList.add(BasketItem.builder() // เพิ่ม Item ไม่มี Options
                .productId(PRODUCT_ID_SAMSUNG_HERO)
                .price(BigDecimal.valueOf(599.00))
                .qty(1)
                .shippingPrice(BigDecimal.valueOf(40.00))
                .build());

        Product productOld = productRepo.findById(PRODUCT_ID_SAMSUNG_HERO).orElse(new Product());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/purchance/checkout/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONArray(basketItemList).toString()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("ok")))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONObject contentJObj = new JSONObject(content);
        JSONObject dataJObj = contentJObj.getJSONObject("data");
        userPurchanceHistoryId = dataJObj.getString("id");
        assertNotNull(userPurchanceHistoryId);
        UserPurchanceHistory userPurchanceHistory = userPurchanceHistoryRepo.findById(userPurchanceHistoryId).orElse(new UserPurchanceHistory());
        assertEquals(userPurchanceHistoryId, userPurchanceHistory.getId());
        assertEquals(STATUS_CHECKOUT, userPurchanceHistory.getStatus());

        List<UserPurchanceHistoryDetail> userPurchanceHistoryDetailList = userPurchanceHistoryDetailRepo.findByUserPurchanceHistoryDetailId_UserPurchanceHistoryId(userPurchanceHistoryId);
        assertTrue(userPurchanceHistoryDetailList.size() > 0);
        assertTrue(userPurchanceHistoryDetailList.stream().anyMatch(it -> PRODUCT_ID_SAMSUNG_HERO.equals(it.getUserPurchanceHistoryDetailId().getProductId())));
        Product product = productRepo.findById(PRODUCT_ID_SAMSUNG_HERO).orElse(new Product());
        assertTrue((productOld.getQty() - 1) == product.getQty());
    }

    @Test
    @Order(5)
    @DisplayName("checkShipping")
    public void flowCheckShipping() throws Exception {
        String id = userPurchanceHistoryId;
        ShippingInfo shippingInfo = ShippingInfo.builder()
                .userPurchanceHistoryId(id)
                .name("Chonlakorn Punphopthaworn")
                .address("xxx")
                .district("xxx")
                .province("xxx")
                .zipcode("10160")
                .mobileNo("0614479979")
                .email("chonlakorn.pun@gmail.com")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/purchance/shipping/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(shippingInfo).toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("ok")))
                .andReturn();

        UserPurchanceHistory userPurchanceHistory = userPurchanceHistoryRepo.findById(id).orElse(new UserPurchanceHistory());
        log.info(userPurchanceHistory.toString());
        assertEquals(STATUS_SHIPPING_INFO, userPurchanceHistory.getStatus());
        assertEquals(shippingInfo.getName(), userPurchanceHistory.getShippingName());
        assertEquals(shippingInfo.getAddress(), userPurchanceHistory.getShippingAddress());
        assertEquals(shippingInfo.getDistrict(), userPurchanceHistory.getShippingDistrict());
        assertEquals(shippingInfo.getProvince(), userPurchanceHistory.getShippingProvince());
        assertEquals(shippingInfo.getZipcode(), userPurchanceHistory.getShippingZipcode());
        assertEquals(shippingInfo.getMobileNo(), userPurchanceHistory.getShippingMobileNo());
        assertEquals(shippingInfo.getEmail(), userPurchanceHistory.getShippingEmail());
    }


    @Test
    @Order(6)
    @DisplayName("payment")
    public void flowPayment() throws Exception {
        String id = userPurchanceHistoryId;
        PurchancePayment shippingInfo = PurchancePayment.builder()
                .userPurchanceHistoryId(id)
                .paymentType(TYPE_CREDIT_CARD)
                .cardNo(CREDIT_CARD_APPROVED)
                .name("test")
                .expireDate(CREDIT_CARD_EXP_DATE)
                .ccv(CREDIT_CARD_CCV)
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/purchance/payment/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(shippingInfo).toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("ok")))
                .andReturn();

        UserPurchanceHistory userPurchanceHistory = userPurchanceHistoryRepo.findById(id).orElse(new UserPurchanceHistory());
        log.info(userPurchanceHistory.toString());
        assertEquals(STATUS_PAID, userPurchanceHistory.getStatus());
    }
}
