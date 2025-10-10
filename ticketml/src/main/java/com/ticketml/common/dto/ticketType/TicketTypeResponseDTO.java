package com.ticketml.common.dto.ticketType;

import lombok.Data;

@Data
public class TicketTypeResponseDTO {
    private Long id;
    private String type;
    private double price;
    private int totalQuantity;
    private int remainingQuantity;
    private String status;
}