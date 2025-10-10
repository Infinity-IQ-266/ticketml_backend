package com.ticketml.common.dto.ticketType;

import com.ticketml.common.enums.TicketTypeStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TicketTypeRequestDTO {
    @NotBlank String type;
    @Positive Double price;
    @Positive Integer totalQuantity;
    TicketTypeStatus ticketTypeStatus = TicketTypeStatus.ACTIVE;
}
