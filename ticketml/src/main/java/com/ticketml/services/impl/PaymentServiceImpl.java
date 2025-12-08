package com.ticketml.services.impl;

import com.ticketml.common.entity.Order;
import com.ticketml.common.entity.OrderItem;
import com.ticketml.common.entity.Ticket;
import com.ticketml.common.entity.TicketType;
import com.ticketml.common.enums.OrderStatus;
import com.ticketml.common.enums.TicketStatus;
import com.ticketml.repository.OrderRepository;
import com.ticketml.repository.TicketRepository;
import com.ticketml.repository.TicketTypeRepository;
import com.ticketml.services.EmailService;
import com.ticketml.services.PaymentService;
import com.ticketml.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final EmailService emailService;

    private final String hashSecret;

    public PaymentServiceImpl(OrderRepository orderRepository, TicketRepository ticketRepository, TicketTypeRepository ticketTypeRepository, EmailService emailService, @Value("${vnpay.hashSecret}") String hashSecret) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.emailService = emailService;
        this.hashSecret = hashSecret;
    }

    @Override
    @Transactional
    public Map<String, String> processVnpayIPN(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            Map<String, String> fields = new HashMap<>();
            Map<String, String[]> params = request.getParameterMap();
            for (String key : params.keySet()) {
                String[] values = params.get(key);
                if (values != null && values.length > 0) {
                    fields.put(key, values[0]);
                }
            }

            String vnp_SecureHash = fields.remove("vnp_SecureHash");
            if (vnp_SecureHash == null) {
                logger.warn("IPN missing vnp_SecureHash!");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return response;
            }

            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
//            hashData.deleteCharAt(hashData.length() - 1);

            String secureHash = VnpayUtil.hmacSHA512(hashSecret, hashData.toString());

            if (!secureHash.equals(vnp_SecureHash)) {
                logger.warn("IPN checksum failed!");
                logger.warn("VNPAY Hash: {}", vnp_SecureHash);
                logger.warn("Generated Hash: {}", secureHash);
                logger.warn("Hash Data: {}", hashData.toString());
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return response;
            }

            String vnp_TxnRef = fields.get("vnp_TxnRef");
            long orderId = Long.parseLong(vnp_TxnRef);
            Order order = orderRepository.findById(orderId).orElse(null);

            if (order == null) {
                logger.warn("IPN Error: Order not found with ID: {}", orderId);
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return response;
            }

            long vnpayAmount = Long.parseLong(fields.get("vnp_Amount")) / 100;
            if (vnpayAmount != (long)order.getTotalPrice()) {
                logger.warn("IPN Error: Invalid amount for Order ID: {}", orderId);
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
                return response;
            }

            if (order.getStatus() != OrderStatus.PENDING) {
                logger.info("IPN Info: Order {} already processed.", orderId);
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            String vnp_ResponseCode = fields.get("vnp_ResponseCode");
            if ("00".equals(vnp_ResponseCode)) {
                logger.info("IPN Success: Payment successful for Order ID: {}", orderId);
                order.setStatus(OrderStatus.CONFIRMED);
                List<Ticket> generatedTickets = new ArrayList<>();
                for (OrderItem item : order.getOrderItems()) {
                    for (int i = 0; i < item.getQuantity(); i++) {
                        Ticket ticket = new Ticket();
                        ticket.setOrderItem(item);
                        ticket.setTicketType(item.getTicketType());
                        ticket.setQrCode(generateTicketCode());
                        ticket.setCheckedIn(false);
                        ticket.setStatus(TicketStatus.ACTIVE);
                        ticketRepository.save(ticket);
                        generatedTickets.add(ticket);
                    }

                    TicketType ticketType = item.getTicketType();
                    int newRemainingQuantity = ticketType.getRemainingQuantity() - item.getQuantity();
                    ticketType.setRemainingQuantity(newRemainingQuantity);
                    ticketTypeRepository.save(ticketType);
                    String recipientEmail = order.getUser().getEmail();
                    emailService.sendTicketEmail(recipientEmail, order, generatedTickets);
                }
            } else {
                logger.warn("IPN Failed: Payment failed for Order ID: {}. ResponseCode: {}", orderId, vnp_ResponseCode);
                order.setStatus(OrderStatus.FAILED);
            }
            orderRepository.save(order);

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

        } catch (Exception e) {
            logger.error("IPN Unknow error", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknow error");
        }
        return response;
    }

    public static String generateTicketCode() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return "TML-" + sb.toString();
    }

}
