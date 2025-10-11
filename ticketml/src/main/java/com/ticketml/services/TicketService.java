package com.ticketml.services;

import com.ticketml.common.dto.ticket.TicketResponseDTO;
import com.ticketml.common.enums.TicketStatus;

import java.util.List;

public interface TicketService {
    List<TicketResponseDTO> getMyTickets(String googleId, TicketStatus status);
}
