package com.ticketml.common.dto.admin;

import com.ticketml.common.enums.OrganizationStatus;
import lombok.Data;

@Data
public class OrgStatusUpdateDTO {
    private OrganizationStatus status;
}