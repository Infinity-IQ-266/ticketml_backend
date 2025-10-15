package com.ticketml.common.dto.event;

import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EventCreateRequestDTO {
    @NotBlank
    String title;
    String description;
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;
    @NotBlank
    String location;

    @NotEmpty
    private List<TicketTypeRequestDTO> ticketTypes;
}
