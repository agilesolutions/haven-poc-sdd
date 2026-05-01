package com.example.serviceb;

import com.example.serviceb.model.InfoEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class InfoControllerIT {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void putAndGetInfo_shouldPersistAndReturnInfo() throws Exception {
        String infoJson = "{\"name\":\"TestName\",\"description\":\"TestDesc\",\"version\":\"1.0.0\"}";
        mockMvc.perform(put("/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(infoJson)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/info")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TestName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("TestDesc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").value("1.0.0"));
    }
}
