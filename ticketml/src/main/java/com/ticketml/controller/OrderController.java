package com.ticketml.controller;

import com.ticketml.common.enums.OrderPaymentResponseDTO;
import com.ticketml.common.enums.OrderRequestDTO;
import com.ticketml.response.Response;
import com.ticketml.services.OrderService;
import com.ticketml.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
//@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Response createOrder(@Valid @RequestBody OrderRequestDTO requestDTO, HttpServletRequest request) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(orderService.createOrderAndGetPaymentUrl(requestDTO, googleId, request));
    }
}
