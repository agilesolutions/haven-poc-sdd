package com.example.serviceb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getInfo_shouldRejectWithoutJwt() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putInfo_shouldRejectWithoutJwt() throws Exception {
        mockMvc.perform(put("/info"))
                .andExpect(status().isUnauthorized());
    }
}
