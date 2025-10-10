package com.ticketml.common.dto.checkIn;

import com.ticketml.common.dto.ticket.TicketDetailResponseDTO;
import com.ticketml.common.enums.CheckInStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckInResponseDTO {
    private CheckInStatus status;
    private String message;
    private TicketDetailResponseDTO ticketDetails;
}
