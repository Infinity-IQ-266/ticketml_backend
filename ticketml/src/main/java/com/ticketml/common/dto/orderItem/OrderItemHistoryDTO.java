package com.ticketml.common.dto.orderItem;

import lombok.Data;

@Data
public class OrderItemHistoryDTO {
    private String eventName;
    private String ticketTypeName;
    private int quantity;
    private double unitPrice;
}
