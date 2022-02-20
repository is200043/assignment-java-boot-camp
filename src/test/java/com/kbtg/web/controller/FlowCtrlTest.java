package com.kbtg.web.controller;

import com.kbtg.db.bean.id.UserItemId;
import com.kbtg.db.dao.UserItemRepo;
import com.kbtg.web.common.bean.BasketItem;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kbtg.web.common.CommonConstants.Test.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private UserItemRepo userItemRepo;

    private MockMvc mockMvc;

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
    @DisplayName("ทดสอบ")
    public void getFlowPurchance() throws Exception {
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

        result = mockMvc.perform(MockMvcRequestBuilders.get("/basket/" + USER_ID_CHONLAKORN))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

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
        result = mockMvc.perform(MockMvcRequestBuilders.put("/basket/" + USER_ID_CHONLAKORN)
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
}
