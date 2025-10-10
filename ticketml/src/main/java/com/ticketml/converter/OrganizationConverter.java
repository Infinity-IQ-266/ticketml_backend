package com.ticketml.converter;

import com.ticketml.common.dto.organization.OrganizationRequestDto;
import com.ticketml.common.entity.Organization;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("organizationConverter")
public class OrganizationConverter extends SuperConverter<OrganizationRequestDto, Organization> {


    private final ModelMapper modelMapper;

    public OrganizationConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public OrganizationRequestDto convertToDTO(Organization entity) {
        return modelMapper.map(entity, OrganizationRequestDto.class);
    }

    @Override
    public Organization convertToEntity(OrganizationRequestDto dto) {
        return modelMapper.map(dto, Organization.class);
    }
}

