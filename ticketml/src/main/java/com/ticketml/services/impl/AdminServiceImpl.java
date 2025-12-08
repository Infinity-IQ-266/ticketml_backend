package com.ticketml.services.impl;

import com.ticketml.common.dto.organization.OrganizationResponseDto;
import com.ticketml.common.dto.organization.OrganizationSearchRequestDto;
import com.ticketml.common.entity.Organization;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.common.enums.OrganizationStatus;
import com.ticketml.converter.OrganizationConverter;
import com.ticketml.exception.BadRequestException;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.OrganizationRepository;
import com.ticketml.services.AdminService;
import com.ticketml.specification.OrganizationSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationConverter organizationConverter;

    public AdminServiceImpl(OrganizationRepository organizationRepository, OrganizationConverter organizationConverter) {
        this.organizationRepository = organizationRepository;
        this.organizationConverter = organizationConverter;
    }


    @Override
    public Page<OrganizationResponseDto> searchOrganizations(OrganizationSearchRequestDto request) {
        Specification<Organization> spec = OrganizationSpecification.hasNameLike(request.getName())
                .and(OrganizationSpecification.hasEmailLike(request.getEmail()))
                .and(OrganizationSpecification.hasStatus(request.getStatus()))
                .and(OrganizationSpecification.hasCreatedAfterOrEqual(request.getCreatedAfter()))
                .and(OrganizationSpecification.hasCreatedBeforeOrEqual(request.getCreatedBefore()));

        Sort sortable = Sort.by(
                Sort.Direction.fromString(request.getDirection().name()),
                request.getAttribute()
        );
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sortable);

        Page<Organization> organizations = organizationRepository.findAll(spec, pageable);

        return organizations.map(organizationConverter::convertToDTO);
    }

    @Override
    public OrganizationResponseDto updateOrganizationStatus(Long orgId, OrganizationStatus status) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ORGANIZATION_NOT_FOUND));

        if (org.getStatus() == OrganizationStatus.INACTIVE && status == OrganizationStatus.PENDING) {
            throw new BadRequestException("Cannot revert INACTIVE organization to PENDING");
        }

        org.setStatus(status);
        Organization savedOrg = organizationRepository.save(org);

        return organizationConverter.convertToDTO(savedOrg);
    }
}
