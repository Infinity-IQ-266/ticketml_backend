package com.ticketml.services;


import com.ticketml.common.dto.order.OrderResponseDTO;
import com.ticketml.common.dto.organization.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrganizationService {
    List<OrganizationResponseDto>  findOrganizationsByCurrentUser(String googleId);

    OrganizationResponseDto createOrganization(String googleId, OrganizationRequestDTO request);

    OrganizationResponseDto updateOrganization(Long orgId, String googleId, OrganizationRequestDTO request);

    // Member Management
    List<MemberResponseDTO> getMembers(Long orgId, String googleId);

    void addMember(Long orgId, String googleId, MemberRequestDTO request);

    void removeMember(Long orgId, Long userId, String googleId);

    Page<OrderResponseDTO> getOrganizationOrders(Long orgId, String googleId, Pageable pageable);

    DashboardStatsDTO getDashboardStats(Long orgId, String googleId);
}
