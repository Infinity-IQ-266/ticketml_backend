package com.ticketml.services;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    Map<String, String> processVnpayIPN(HttpServletRequest request);
}
