package com.ticketml.services.impl;

import com.ticketml.common.dto.organization.OrganizationRequestDto;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.converter.OrganizationConverter;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.OrganizerMembershipRepository;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.OrganizationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final UserRepository userRepository;
    private final OrganizerMembershipRepository organizerMembershipRepository;
    private final OrganizationConverter organizationConverter;

    public OrganizationServiceImpl(UserRepository userRepository, OrganizerMembershipRepository organizerMembershipRepository,  OrganizationConverter organizationConverter) {
        this.userRepository = userRepository;
        this.organizerMembershipRepository = organizerMembershipRepository;
        this.organizationConverter = organizationConverter;
    }

    @Override
    public List<OrganizationRequestDto> findOrganizationsByCurrentUser(String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));
        return organizerMembershipRepository.findByUser(currentUser)
                .stream()
                .map(membership -> organizationConverter.convertToDTO(membership.getOrganization()))
                .collect(Collectors.toList());
    }
}
