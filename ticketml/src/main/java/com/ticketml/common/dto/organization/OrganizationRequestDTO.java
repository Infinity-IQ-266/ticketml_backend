package com.ticketml.common.dto.organization;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OrganizationRequestDTO {
    private String name;

    private String description;

    private String email;

    private String phoneNumber;

    private String address;

    private MultipartFile logo;

}