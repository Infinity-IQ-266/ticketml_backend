package com.ticketml.services;

import com.ticketml.common.dto.event.*;
import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import com.ticketml.common.dto.ticketType.TicketTypeResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EventService {
    List<EventDetailResponseDTO> findByOrganizationId(Long organizationId, String googleId);
    EventDetailResponseDTO createEventWithTickets(Long organizationId, EventCreateRequestDTO requestDTO, String googleId);
    EventDetailResponseDTO updateEvent(Long eventId, EventUpdateRequestDTO requestDTO, String googleId);
    TicketTypeResponseDTO addTicketTypeToEvent(Long eventId, TicketTypeRequestDTO requestDTO, String googleId);

    Page<EventDetailResponseDTO> searchEvent(EventSearchRequestDto request);

    EventDetailResponseDTO findEventById(Long eventId);
}
