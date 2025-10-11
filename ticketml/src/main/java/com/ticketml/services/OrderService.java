package com.ticketml.services;


import com.ticketml.common.dto.order.OrderResponseDTO;
import com.ticketml.common.enums.OrderPaymentResponseDTO;
import com.ticketml.common.dto.order.OrderRequestDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    OrderPaymentResponseDTO createOrderAndGetPaymentUrl(OrderRequestDTO requestDTO, String googleId, HttpServletRequest request);

    List<OrderResponseDTO> getMyOrderHistory(String googleId);
}
