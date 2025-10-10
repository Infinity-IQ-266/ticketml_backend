package com.ticketml.services;


import com.ticketml.common.dto.organization.OrganizationRequestDto;

import java.util.List;

public interface OrganizationService {
    List<OrganizationRequestDto>  findOrganizationsByCurrentUser(String googleId);
}
