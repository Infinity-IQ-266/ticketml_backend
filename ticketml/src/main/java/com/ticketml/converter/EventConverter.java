package com.ticketml.converter;

import com.ticketml.common.dto.event.EventCreateRequestDTO;
import com.ticketml.common.dto.event.EventDetailResponseDTO;
import com.ticketml.common.entity.Event;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("eventConverter")
public class EventConverter extends Super2Converter<EventCreateRequestDTO, EventDetailResponseDTO, Event> {


    private final ModelMapper modelMapper;

    public EventConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public EventDetailResponseDTO convertToResponseDTO(Event entity) {
        EventDetailResponseDTO eventDetailResponseDTO =  modelMapper.map(entity, EventDetailResponseDTO.class);
        eventDetailResponseDTO.setOrganizationId(entity.getOrganization().getId());
        eventDetailResponseDTO.setOrganizationName(entity.getOrganization().getName());
        return eventDetailResponseDTO;
    }

    @Override
    public Event convertRequestToEntity(EventCreateRequestDTO dto) {
        return modelMapper.map(dto, Event.class);
    }

    @Override
    public EventCreateRequestDTO convertEntityToRequest(Event request) {
        return modelMapper.map(request, EventCreateRequestDTO.class);
    }
}
