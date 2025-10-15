package com.ticketml.common.dto.event;

import com.ticketml.common.dto.ticketType.TicketTypeResponseDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class EventDetailResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private Long organizationId;
    private String organizationName;

    private List<TicketTypeResponseDTO> ticketTypes;
}
