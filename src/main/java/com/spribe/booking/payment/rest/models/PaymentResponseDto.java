package com.spribe.booking.payment.rest.models;

import com.spribe.booking.infrastructure.util.type.PaymentStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private Long bookingId;
    private Double amount;
    private PaymentStatusType status;
    private Boolean isRefunded;
}
