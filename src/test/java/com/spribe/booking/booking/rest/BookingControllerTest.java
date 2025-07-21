package com.spribe.booking.booking.rest;

import com.spribe.booking.TestCacheConfig;
import com.spribe.booking.booking.rest.models.BookingRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
@Import(TestCacheConfig.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test void bookUnit() {
        BookingRequestDto requestDto = new BookingRequestDto();
        String requestBody = """
        {
            "userId": 3,
            "unitId": 2,
            "startDate": 2025-08-20
            "endDate": 2025-08-23
        }
    """;

//        mockMvc.perform(post("/api/v1/booking")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonRequest))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test void cancelBooking() {
    }

    @Test void getAvailableUnitCountToBook() {
    }
}
