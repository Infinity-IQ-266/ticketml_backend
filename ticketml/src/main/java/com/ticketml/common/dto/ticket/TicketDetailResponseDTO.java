package com.ticketml.common.dto.ticket;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketDetailResponseDTO {
    private String userName;
    private String ticketType;
    private LocalDate checkInTime;
}

