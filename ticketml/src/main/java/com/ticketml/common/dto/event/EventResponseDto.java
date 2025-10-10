package com.ticketml.common.dto.event;

import com.ticketml.common.entity.TicketType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventResponseDto {
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private List<TicketType> ticketTypes = new ArrayList<>();
}
