package com.digitalchina.mscx.api.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by xiaoning.sun on 2016/12/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApiOrderControllerTest {
    @Autowired
    private MockMvc mvc;
    @Before
    public void setUp(){

    }

    @Test
    public void getOrderListByPKTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/order/getOrderListByPK.do").param("id", "1")).andExpect(status().isOk()).andDo(print());
    }


}
