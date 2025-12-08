package com.ticketml.controller;

import com.ticketml.common.dto.admin.OrgStatusUpdateDTO;
import com.ticketml.common.dto.organization.OrganizationSearchRequestDto;
import com.ticketml.common.enums.DirectionEnum;
import com.ticketml.common.enums.OrganizationStatus;
import com.ticketml.response.Response;
import com.ticketml.services.AdminService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/organizations")
    public Response getAllOrganizations(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(name = "direction", required = false, defaultValue = "DESC") DirectionEnum direction,
            @RequestParam(name = "attribute", required = false, defaultValue = "createdAt") String attribute,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "status", required = false, defaultValue = "ACTIVE") OrganizationStatus status,
            @RequestParam(name = "createdAfter", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAfter,
            @RequestParam(name = "createdBefore", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdBefore
    ) {
        return new Response(adminService.searchOrganizations(
                OrganizationSearchRequestDto.builder()
                        .page(page)
                        .size(size)
                        .direction(direction)
                        .attribute(attribute)
                        .name(name)
                        .email(email)
                        .status(status)
                        .createdAfter(createdAfter)
                        .createdBefore(createdBefore)
                        .build()
        ));
    }

    @PatchMapping("/organizations/{orgId}/status")
    public Response updateOrgStatus(@PathVariable Long orgId, @RequestBody OrgStatusUpdateDTO request) {
        return new Response(adminService.updateOrganizationStatus(orgId, request.getStatus()));
    }
}

