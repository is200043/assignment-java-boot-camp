package com.kbtg.web.controller;

import com.kbtg.web.common.bean.ShippingInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.PrintStream;

import static com.kbtg.web.common.CommonConstants.Test.USER_ID_CHONLAKORN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PurchanceCtrlTest {

    @Autowired
    private WebApplicationContext wac;

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
    @DisplayName("ทดสอบ checkShipping Email ผิด")
    public void checkShippingValidateEmail() throws Exception {
        ShippingInfo shippingInfo = ShippingInfo.builder()
                .userPurchanceHistoryId("xxx")
                .name("xxxxxxxxxxxxxxxxx")
                .address("xxx")
                .district("xxx")
                .province("xxx")
                .zipcode("10160")
                .mobileNo("0614479979")
                .email("test")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/purchance/shipping/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(shippingInfo).toString()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("error")))
                .andExpect(jsonPath("$.data.error_description", is("Email should be valid")))
                .andReturn();
    }

    @Test
    @Order(2)
    @DisplayName("ทดสอบ checkShipping เบอร์โทร ผิด")
    public void checkShippingValidateMobileNo() throws Exception {
        ShippingInfo shippingInfo = ShippingInfo.builder()
                .userPurchanceHistoryId("xxx")
                .name("Chonlakorn Punphopthaworn")
                .address("xxx")
                .district("xxx")
                .province("xxx")
                .zipcode("10160")
                .mobileNo("061979")
                .email("test@test.com")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/purchance/shipping/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(shippingInfo).toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("error")))
                .andExpect(jsonPath("$.data.error_description", is("Mobile no should be valid")))
                .andReturn();
    }
}
