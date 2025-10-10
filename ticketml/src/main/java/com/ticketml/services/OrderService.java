package com.ticketml.services;


import com.ticketml.common.enums.OrderPaymentResponseDTO;
import com.ticketml.common.enums.OrderRequestDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    OrderPaymentResponseDTO createOrderAndGetPaymentUrl(OrderRequestDTO requestDTO, String googleId, HttpServletRequest request);
}
