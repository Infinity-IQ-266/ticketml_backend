package com.ticketml.common.dto.order;

import com.ticketml.common.dto.orderItem.OrderItemHistoryDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private double totalPrice;
    private String status;
    private LocalDate createdAt;
    private List<OrderItemHistoryDTO> items;
}
