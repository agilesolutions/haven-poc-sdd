package com.example.servicea;

import com.example.servicea.dto.InfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class InfoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getInfo_shouldReturnInfo_whenServiceBMocked() throws Exception {
        // TODO: Mock InfoClient to return a sample InfoDto
        mockMvc.perform(get("/info")
                .header("Authorization", "Bearer test-token")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.version").exists());
    }
}
