package com.ticketml.converter;

import com.ticketml.common.dto.orderItem.OrderItemHistoryDTO;

import com.ticketml.common.entity.OrderItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("orderItemConverter")
public class OrderItemConverter extends SuperConverter<OrderItemHistoryDTO, OrderItem> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderItemHistoryDTO convertToDTO(OrderItem entity) {
        OrderItemHistoryDTO response = modelMapper.map(entity, OrderItemHistoryDTO.class);
        response.setEventName(entity.getTicketType().getEvent().getTitle());
        response.setTicketTypeName(entity.getTicketType().getType());
        return response;
    }

    @Override
    public OrderItem convertToEntity(OrderItemHistoryDTO dto) {
        return modelMapper.map(dto, OrderItem.class);
    }
}
