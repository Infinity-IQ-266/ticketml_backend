package com.ticketml.common.dto.order;

import com.ticketml.common.dto.orderItem.OrderItemRequestDTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    @NotEmpty
    private List<OrderItemRequestDTO> items;
}
