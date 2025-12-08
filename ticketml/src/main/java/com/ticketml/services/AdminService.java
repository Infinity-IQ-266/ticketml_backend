package com.ticketml.services;

import com.ticketml.common.dto.organization.OrganizationResponseDto;
import com.ticketml.common.dto.organization.OrganizationSearchRequestDto;
import com.ticketml.common.enums.OrganizationStatus;
import org.springframework.data.domain.Page;

public interface AdminService {
    Page<OrganizationResponseDto> searchOrganizations(OrganizationSearchRequestDto request);
    OrganizationResponseDto updateOrganizationStatus(Long orgId, OrganizationStatus status);
}
