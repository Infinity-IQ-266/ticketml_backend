package com.ticketml.services;


import com.ticketml.common.dto.organization.OrganizationRequestDTO;
import com.ticketml.common.dto.organization.OrganizationResponseDto;

import java.util.List;

public interface OrganizationService {
    List<OrganizationResponseDto>  findOrganizationsByCurrentUser(String googleId);

    OrganizationResponseDto createOrganization(String googleId, OrganizationRequestDTO request);
    OrganizationResponseDto updateOrganization(Long orgId, String googleId, OrganizationRequestDTO request);
}
