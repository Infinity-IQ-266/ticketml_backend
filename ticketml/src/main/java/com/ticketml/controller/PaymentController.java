package com.ticketml.controller;

import com.ticketml.response.Response;
import com.ticketml.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vnpay-ipn")
    public Response handleVnpayIPN(HttpServletRequest request) {
        return new Response(paymentService.processVnpayIPN(request));
    }
}
