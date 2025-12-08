package com.ticketml.common.dto.organization;

import lombok.Data;

@Data
public class OrganizationResponseDto {

    private Long organizationId;

    private String name;

    private String description;

    private String logoUrl;

    private String email;

    private String phoneNumber;

    private String address;
}
