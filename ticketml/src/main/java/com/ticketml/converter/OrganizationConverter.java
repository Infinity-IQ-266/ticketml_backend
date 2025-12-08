package com.ticketml.converter;

import com.ticketml.common.dto.organization.OrganizationResponseDto;
import com.ticketml.common.entity.Organization;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("organizationConverter")
public class OrganizationConverter extends SuperConverter<OrganizationResponseDto, Organization> {


    private final ModelMapper modelMapper;

    public OrganizationConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public OrganizationResponseDto convertToDTO(Organization entity) {
        OrganizationResponseDto dto = modelMapper.map(entity, OrganizationResponseDto.class);
        dto.setOrganizationId(entity.getId());
        return dto;
    }

    @Override
    public Organization convertToEntity(OrganizationResponseDto dto) {
        return modelMapper.map(dto, Organization.class);
    }
}

