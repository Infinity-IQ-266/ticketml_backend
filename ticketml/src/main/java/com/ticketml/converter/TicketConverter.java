package com.ticketml.converter;

import com.ticketml.common.dto.ticket.TicketResponseDTO;
import com.ticketml.common.entity.Ticket;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("ticketConverter")
public class TicketConverter extends  SuperConverter<TicketResponseDTO, Ticket> {

    private final ModelMapper modelMapper;

    public TicketConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TicketResponseDTO convertToDTO(Ticket entity) {
        TicketResponseDTO response = modelMapper.map(entity, TicketResponseDTO.class);
        response.setEventId(entity.getTicketType().getEvent().getId());
        response.setEventName(entity.getTicketType().getEvent().getTitle());
        response.setEventLocation(entity.getTicketType().getEvent().getLocation());
        response.setEventStartDate(entity.getTicketType().getEvent().getStartDate());
        response.setTicketTypeName(entity.getTicketType().getType());
        response.setOrderId(entity.getOrderItem().getOrder().getId());
        response.setTicketStatus(entity.getStatus());
        return response;
    }

    @Override
    public Ticket convertToEntity(TicketResponseDTO dto) {
        return modelMapper.map(dto, Ticket.class);
    }
}
