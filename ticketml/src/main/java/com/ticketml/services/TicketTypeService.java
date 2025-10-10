package com.ticketml.services;

import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import com.ticketml.common.dto.ticketType.TicketTypeResponseDTO;

public interface TicketTypeService {
    TicketTypeResponseDTO updateTicketType(Long ticketTypeId, TicketTypeRequestDTO requestDTO, String googleId);
}
