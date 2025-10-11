package com.ticketml.converter;

import com.ticketml.common.dto.orderItem.OrderItemHistoryDTO;
import com.ticketml.common.dto.order.OrderResponseDTO;
import com.ticketml.common.entity.Order;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("orderConverter")
public class OrderConverter extends SuperConverter<OrderResponseDTO, Order> {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderItemConverter orderItemConverter;

    @Override
    public OrderResponseDTO convertToDTO(Order entity) {
        OrderResponseDTO response = modelMapper.map(entity, OrderResponseDTO.class);
        response.setCreatedAt(entity.getCreatedAt().toLocalDate());
        OrderItemHistoryDTO orderItemDto = null;
        List<OrderItemHistoryDTO> itemDTOs = entity.getOrderItems().stream()
                .map(orderItemConverter::convertToDTO)
                .collect(Collectors.toList());
        response.setItems(itemDTOs);
        return response;
    }

    @Override
    public Order convertToEntity(OrderResponseDTO dto) {
        return modelMapper.map(dto, Order.class);
    }
}
