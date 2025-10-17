package com.ticketml.services.impl;

import com.ticketml.common.dto.orderItem.OrderItemRequestDTO;
import com.ticketml.common.dto.order.OrderResponseDTO;
import com.ticketml.common.entity.Order;
import com.ticketml.common.entity.OrderItem;
import com.ticketml.common.entity.TicketType;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.common.enums.OrderPaymentResponseDTO;
import com.ticketml.common.dto.order.OrderRequestDTO;
import com.ticketml.common.enums.OrderStatus;
import com.ticketml.converter.OrderConverter;
import com.ticketml.exception.BadRequestException;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.OrderItemRepository;
import com.ticketml.repository.OrderRepository;
import com.ticketml.repository.TicketTypeRepository;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.OrderService;
import com.ticketml.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final VnpayUtil vnpayUtil;
    private final OrderConverter orderConverter;

    private final String tmnCode;
    private final String hashSecret;
    private final String paymentUrl;
    private final String returnUrl;
    private final String ipnUrl;

    public OrderServiceImpl(UserRepository userRepository,
                            TicketTypeRepository ticketTypeRepository,
                            OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            VnpayUtil vnpayUtil, OrderConverter orderConverter,
                            @Value("${vnpay.tmnCode}") String tmnCode,
                            @Value("${vnpay.hashSecret}") String hashSecret,
                            @Value("${vnpay.paymentUrl}") String paymentUrl,
                            @Value("${vnpay.returnUrl}") String returnUrl,
                            @Value("${vnpay.ipnUrl}") String ipnUrl) {
        this.userRepository = userRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.vnpayUtil = vnpayUtil;
        this.orderConverter = orderConverter;
        this.tmnCode = tmnCode;
        this.hashSecret = hashSecret;
        this.paymentUrl = paymentUrl;
        this.returnUrl = returnUrl;
        this.ipnUrl = ipnUrl;
    }

    @Override
    @Transactional
    public OrderPaymentResponseDTO createOrderAndGetPaymentUrl(OrderRequestDTO requestDTO, String googleId, HttpServletRequest request) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (requestDTO.getFullName() != null) {
            currentUser.setFullName(requestDTO.getFullName());
        }

        if (requestDTO.getEmail() != null) {
            currentUser.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(requestDTO.getPhoneNumber());
        }


        double totalPrice = 0;

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO itemDTO : requestDTO.getItems()) {
            TicketType ticketType = ticketTypeRepository.findById(itemDTO.getTicketTypeId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.TICKET_TYPE_NOT_FOUND));

            if (ticketType.getRemainingQuantity() < itemDTO.getQuantity()) {
                throw new BadRequestException(ErrorMessage.QUANTITY_NOT_ENOUGH, "Not enough tickets available for type: " + ticketType.getType());
            }

            totalPrice += ticketType.getPrice() * itemDTO.getQuantity();

            OrderItem orderItem = new OrderItem();
            orderItem.setTicketType(ticketType);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(ticketType.getPrice());
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        userRepository.save(currentUser);
        orderItemRepository.saveAll(orderItems);

        // VNPAY

        String vnp_TxnRef = String.valueOf(savedOrder.getId());
        String vnp_Amount = String.valueOf((long) totalPrice * 100);
        String orderInfo = "Thanh toan don hang " + vnp_TxnRef;
        String ipAddress = VnpayUtil.getIpAddress(request);


        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo.replace(" ", "+"));
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);

        Calendar cld = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayUtil.hmacSHA512(hashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String finalPaymentUrl = paymentUrl + "?" + queryUrl;

        return OrderPaymentResponseDTO.builder()
                .orderId(savedOrder.getId())
                .totalPrice(totalPrice)
                .paymentUrl(finalPaymentUrl)
                .build();
    }

    @Override
    public List<OrderResponseDTO> getMyOrderHistory(String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(currentUser);

        return orders.stream()
                .map(orderConverter::convertToDTO)
                .collect(Collectors.toList());
    }
}
