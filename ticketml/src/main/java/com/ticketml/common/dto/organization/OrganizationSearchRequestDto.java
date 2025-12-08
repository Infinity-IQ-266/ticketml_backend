package com.ticketml.common.dto.organization;


import com.ticketml.common.enums.DirectionEnum;
import com.ticketml.common.enums.OrganizationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OrganizationSearchRequestDto {
    private Integer page;

    private Integer size;

    private DirectionEnum direction;

    private String attribute;

    private String name;

    private String email;

    private OrganizationStatus status;

    private LocalDate createdAfter;

    private LocalDate createdBefore;
}

