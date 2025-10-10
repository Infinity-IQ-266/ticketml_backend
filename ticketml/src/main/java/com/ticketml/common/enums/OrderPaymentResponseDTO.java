package com.ticketml.common.enums;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderPaymentResponseDTO {
    private Long orderId;
    private double totalPrice;
    private String paymentUrl;
}