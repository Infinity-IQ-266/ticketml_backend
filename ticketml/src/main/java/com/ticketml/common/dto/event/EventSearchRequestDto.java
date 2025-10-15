package com.ticketml.common.dto.event;

import com.ticketml.common.enums.DirectionEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EventSearchRequestDto {
    private int page;
    private int size;
    private DirectionEnum direction;
    private String attribute;

    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
}

