package com.ticketml.common.dto.ticket;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketDetailResponseDTO {
    private String userName;
    private String ticketType;
    private LocalDateTime checkInTime;
}

