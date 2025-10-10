package com.ticketml.common.dto.event;

import com.ticketml.common.dto.ticketType.TicketTypeResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDetailResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Long organizationId;
    private String organizationName;

    private List<TicketTypeResponseDTO> ticketTypes;
}
