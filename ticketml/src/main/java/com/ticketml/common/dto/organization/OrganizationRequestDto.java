package com.ticketml.common.dto.organization;

import lombok.Data;

@Data
public class OrganizationRequestDto {
    private String name;
    private String description;
    private String logoUrl;
}
