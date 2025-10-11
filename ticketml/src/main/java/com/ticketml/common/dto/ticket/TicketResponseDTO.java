package com.ticketml.common.dto.ticket;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketResponseDTO {
    private Long id;
    private String qrCode;
    private boolean isCheckedIn;

    private Long eventId;
    private String eventName;
    private String eventLocation;
    private LocalDate eventStartDate;

    private String ticketTypeName;

    private Long orderId;
}
