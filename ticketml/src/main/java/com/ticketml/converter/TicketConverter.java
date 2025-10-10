package com.ticketml.converter;

import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import com.ticketml.common.dto.ticketType.TicketTypeResponseDTO;
import com.ticketml.common.entity.TicketType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("ticketConverter")
public class TicketConverter extends Super2Converter<TicketTypeRequestDTO, TicketTypeResponseDTO, TicketType> {


    private final ModelMapper modelMapper;

    public TicketConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public TicketTypeResponseDTO convertToResponseDTO(TicketType entity) {
        return modelMapper.map(entity, TicketTypeResponseDTO.class);
    }

    @Override
    public TicketType convertRequestToEntity(TicketTypeRequestDTO dto) {
        TicketType ticketType = modelMapper.map(dto, TicketType.class);
        ticketType.setRemainingQuantity(dto.getTotalQuantity());
        ticketType.setStatus(dto.getTicketTypeStatus());
        return ticketType;
    }

    @Override
    public TicketTypeRequestDTO convertEntityToRequest(TicketType request) {
        return modelMapper.map(request, TicketTypeRequestDTO.class);
    }
}
