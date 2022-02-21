package com.kbtg.web.controller;

import com.kbtg.web.common.bean.ShippingInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
    private ShippingInfo shippingInfo;

    @BeforeAll
    public void beforeAll() throws Exception {
        System.setOut(new PrintStream(System.out, true, UTF_8.name()));
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(((request, response, chain) -> {
            response.setCharacterEncoding(UTF_8.name());
            chain.doFilter(request, response);
        })).build();

        shippingInfo = ShippingInfo.builder()
                .userPurchanceHistoryId("01cb66fa-8f80-46b6-8a85-7b4ba617b89a")
                .name("Chonlakorn Punphopthaworn")
                .address("xxx")
                .district("xxx")
                .province("xxx")
                .zipcode("10160")
                .mobileNo("0614479979")
                .email("test@test.com")
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("ทดสอบ checkShipping Email ผิด")
    public void checkShippingValidateEmail() throws Exception {
        ShippingInfo si = new ShippingInfo();
        BeanUtils.copyProperties(shippingInfo, si);
        si.setEmail("test");

        mockMvc.perform(MockMvcRequestBuilders.post("/purchance/shipping/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(si).toString()))
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
        ShippingInfo si = new ShippingInfo();
        BeanUtils.copyProperties(shippingInfo, si);
        si.setMobileNo("061979");

        mockMvc.perform(MockMvcRequestBuilders.post("/purchance/shipping/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(si).toString()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("error")))
                .andExpect(jsonPath("$.data.error_description", is("Mobile no should be valid")))
                .andReturn();
    }

    @Test
    @Order(3)
    @DisplayName("ทดสอบ checkShipping ไม่พบเลขคำสั่งซื้อ")
    public void checkShippingIdNotFound() throws Exception {
        ShippingInfo si = new ShippingInfo();
        BeanUtils.copyProperties(shippingInfo, si);
        si.setUserPurchanceHistoryId("test");

        mockMvc.perform(MockMvcRequestBuilders.post("/purchance/shipping/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(si).toString()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("error")))
                .andExpect(jsonPath("$.data.error_description", is("User Purchance History not found")))
                .andReturn();
    }

    @Test
    @Order(4)
    @DisplayName("ทดสอบ checkShipping ผ่าน")
    public void checkShippingSuccess() throws Exception {
        ShippingInfo si = new ShippingInfo();
        BeanUtils.copyProperties(shippingInfo, si);

        mockMvc.perform(MockMvcRequestBuilders.post("/purchance/shipping/" + USER_ID_CHONLAKORN)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new JSONObject(si).toString()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data.status", is("ok")))
                .andReturn();
    }
}
