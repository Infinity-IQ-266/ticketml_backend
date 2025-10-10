package com.ticketml.common.dto.event;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventUpdateRequestDTO {

    String title;

    String description;

    String location;

    LocalDate startDate;

    LocalDate endDate;
}
