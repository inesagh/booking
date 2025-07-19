package com.spribe.booking.payment.rest.models;

import com.spribe.booking.util.type.PaymentStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private Long bookingId;
    private Double amount;
    private PaymentStatusType status;
    private Boolean isRefunded;
}
