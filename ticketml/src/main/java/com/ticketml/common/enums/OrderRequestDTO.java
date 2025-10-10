package com.ticketml.common.enums;

import com.ticketml.common.dto.OrderItemRequestDTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    @NotEmpty
    private List<OrderItemRequestDTO> items;
}
