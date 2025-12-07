package com.ticketml.services.impl;

import com.ticketml.common.dto.organization.OrganizationRequestDTO;
import com.ticketml.common.dto.organization.OrganizationResponseDto;
import com.ticketml.common.entity.Organization;
import com.ticketml.common.entity.OrganizerMembership;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.common.enums.OrganizationStatus;
import com.ticketml.common.enums.OrganizerRole;
import com.ticketml.converter.OrganizationConverter;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.OrganizationRepository;
import com.ticketml.repository.OrganizerMembershipRepository;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.OrganizationService;
import com.ticketml.services.S3Service;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final UserRepository userRepository;
    private final OrganizerMembershipRepository organizerMembershipRepository;
    private final OrganizationConverter organizationConverter;
    private final S3Service s3Service;
    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(UserRepository userRepository, OrganizerMembershipRepository organizerMembershipRepository, OrganizationConverter organizationConverter, S3Service s3Service, OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.organizerMembershipRepository = organizerMembershipRepository;
        this.organizationConverter = organizationConverter;
        this.s3Service = s3Service;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public List<OrganizationResponseDto> findOrganizationsByCurrentUser(String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));
        return organizerMembershipRepository.findByUser(currentUser)
                .stream()
                .map(membership -> organizationConverter.convertToDTO(membership.getOrganization()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganizationResponseDto createOrganization(String googleId, OrganizationRequestDTO request) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        Organization org = new Organization();
        org.setName(request.getName());
        org.setDescription(request.getDescription());
        org.setEmail(request.getEmail());
        org.setPhoneNumber(request.getPhoneNumber());
        org.setAddress(request.getAddress());
        org.setStatus(OrganizationStatus.PENDING);

        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            String logoUrl = s3Service.uploadFile(request.getLogo());
            org.setLogoUrl(logoUrl);
        }

        Organization savedOrg = organizationRepository.save(org);

        OrganizerMembership membership = new OrganizerMembership();
        membership.setUser(user);
        membership.setOrganization(savedOrg);
        membership.setRoleInOrg(OrganizerRole.OWNER);
        organizerMembershipRepository.save(membership);

        return organizationConverter.convertToDTO(savedOrg);
    }

    @Override
    @Transactional
    public OrganizationResponseDto updateOrganization(Long orgId, String googleId, OrganizationRequestDTO request) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ORGANIZATION_NOT_FOUND));

        if (request.getName() != null) org.setName(request.getName());
        if (request.getDescription() != null) org.setDescription(request.getDescription());
        if (request.getEmail() != null) org.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) org.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) org.setAddress(request.getAddress());

        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            String logoUrl = s3Service.uploadFile(request.getLogo());
            org.setLogoUrl(logoUrl);
        }

        return organizationConverter.convertToDTO(organizationRepository.save(org));
    }
}
