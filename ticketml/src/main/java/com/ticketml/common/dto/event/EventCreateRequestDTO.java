package com.ticketml.common.dto.event;

import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventCreateRequestDTO {
    @NotBlank String title;
    String description;
    @NotNull LocalDateTime startDate;
    @NotNull LocalDateTime endDate;
    @NotBlank String location;

    @NotEmpty
    private List<TicketTypeRequestDTO> ticketTypes;
}
