package com.spribe.booking.unit.rest;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.helper.UnitHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.spribe.booking")
@ActiveProfiles("test")
@Import(TestCacheConfig.class)
class UnitControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UnitHelper unitHelper;

    @AfterEach
    void tearDown() {
        unitHelper.cleanAll();
    }

    @Test void addUnitTest() throws Exception {
        String requestBody = """
        {
            "roomsCount": 2,
            "type": "HOME",
            "floor": 3,
            "price": 150.0,
            "description": "Test unit from integration test",
            "available": true,
            "userId": 1
        }
    """;

        mockMvc.perform(post("/api/v1/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomsCount").value(2))
                .andExpect(jsonPath("$.type").value("HOME"))
                .andExpect(jsonPath("$.floor").value(3));
    }

    @Test void getUnitsWithFilterAndPaginationTest() throws Exception {
        unitHelper.createUnit();
        this.mockMvc.perform(get("/api/v1/units")
                        .param("available", "true")
                        .param("page", "0")
                        .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").isNumber())
                    .andExpect(jsonPath("$.content[0].available").value(true))
                    .andExpect(jsonPath("$.content[0].type").value("HOME"));
    }
}
