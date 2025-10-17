package com.ticketml.common.dto.order;

import com.ticketml.common.dto.orderItem.OrderItemRequestDTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    private String fullName;

    private String email;

    private String phoneNumber;

    @NotEmpty
    private List<OrderItemRequestDTO> items;
}
