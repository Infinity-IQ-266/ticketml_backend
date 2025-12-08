package com.ticketml.services.impl;

import com.ticketml.common.dto.order.OrderResponseDTO;
import com.ticketml.common.dto.organization.*;
import com.ticketml.common.entity.*;
import com.ticketml.common.enums.*;
import com.ticketml.converter.OrderConverter;
import com.ticketml.converter.OrganizationConverter;
import com.ticketml.exception.BadRequestException;
import com.ticketml.exception.ForbiddenException;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.*;
import com.ticketml.services.OrganizationService;
import com.ticketml.services.S3Service;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final UserRepository userRepository;
    private final OrganizerMembershipRepository membershipRepository;
    private final OrganizationConverter organizationConverter;
    private final S3Service s3Service;
    private final OrganizationRepository organizationRepository;
    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;
    private final TicketRepository ticketRepository;

    public OrganizationServiceImpl(UserRepository userRepository,
                                   OrganizerMembershipRepository membershipRepository,
                                   OrganizationConverter organizationConverter,
                                   S3Service s3Service,
                                   OrganizationRepository organizationRepository,
                                   OrderRepository orderRepository,
                                   OrderConverter orderConverter,
                                   TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.organizationConverter = organizationConverter;
        this.s3Service = s3Service;
        this.organizationRepository = organizationRepository;
        this.orderRepository = orderRepository;
        this.orderConverter = orderConverter;
        this.ticketRepository = ticketRepository;
    }

    private void validateOrganizerAccess(Long orgId, String googleId, List<OrganizerRole> allowedRoles) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        OrganizerMembership membership = membershipRepository.findByUserAndOrganizationId(user, orgId)
                .orElseThrow(() -> new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY,
                        "You are not a member of this organization"));

        if (membership.getOrganization().getStatus() != OrganizationStatus.ACTIVE) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY,
                    "Organization is not active or has been banned.");
        }

        if (allowedRoles != null && !allowedRoles.isEmpty() &&
                !allowedRoles.contains(membership.getRoleInOrg())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY,
                    "You do not have permission to perform this action.");
        }

    }

    @Override
    public List<OrganizationResponseDto> findOrganizationsByCurrentUser(String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
        return membershipRepository.findByUser(currentUser)
                .stream()
                .map(membership -> organizationConverter.convertToDTO(membership.getOrganization()))
                .collect(Collectors.toList());
    }

    @Override
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
        membershipRepository.save(membership);

        return organizationConverter.convertToDTO(savedOrg);
    }

    @Override
    public OrganizationResponseDto updateOrganization(Long orgId, String googleId, OrganizationRequestDTO request) {
        validateOrganizerAccess(orgId, googleId, List.of(OrganizerRole.OWNER));

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

    @Override
    public List<MemberResponseDTO> getMembers(Long orgId, String googleId) {
        validateOrganizerAccess(orgId, googleId, null);

        return membershipRepository.findByOrganizationIdWithUser(orgId).stream()
                .map(m -> MemberResponseDTO.builder()
                        .userId(m.getUser().getId())
                        .email(m.getUser().getEmail())
                        .fullName(m.getUser().getFullName())
                        .role(m.getRoleInOrg())
                        .status(m.getStatus())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public void addMember(Long orgId, String googleId, MemberRequestDTO request) {
        validateOrganizerAccess(orgId, googleId, List.of(OrganizerRole.OWNER, OrganizerRole.MANAGER));

        User targetUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.USER_NOT_FOUND,
                        "User with this email not found"));

        if (membershipRepository.existsByUserIdAndOrganizationId(targetUser.getId(), orgId)) {
            throw new BadRequestException(ErrorMessage.BAD_REQUEST,
                    "User is already a member of this organization");
        }

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ORGANIZATION_NOT_FOUND));

        OrganizerMembership newMember = new OrganizerMembership();
        newMember.setUser(targetUser);
        newMember.setOrganization(org);
        newMember.setRoleInOrg(request.getRole());
        newMember.setStatus(MembershipStatus.ACTIVE);

        membershipRepository.save(newMember);
    }

    @Override
    public void removeMember(Long orgId, Long targetUserId, String googleId) {
        validateOrganizerAccess(orgId, googleId, List.of(OrganizerRole.OWNER));

        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (currentUser.getId().equals(targetUserId)) {
            throw new BadRequestException(ErrorMessage.BAD_REQUEST,
                    "Cannot remove yourself. Use 'Leave Organization' feature instead.");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        OrganizerMembership targetMembership = membershipRepository.findByUserAndOrganizationId(targetUser, orgId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_NOT_FOUND,
                        "Member not found in this organization"));

        if (targetMembership.getRoleInOrg() == OrganizerRole.OWNER) {
            long ownerCount = membershipRepository.countByOrganizationIdAndRole(orgId, OrganizerRole.OWNER);
            if (ownerCount <= 1) {
                throw new BadRequestException(ErrorMessage.BAD_REQUEST,
                        "Cannot remove the last owner. Please assign another owner first.");
            }
        }

        membershipRepository.delete(targetMembership);
    }

    @Override
    public Page<OrderResponseDTO> getOrganizationOrders(Long orgId, String googleId, Pageable pageable) {
        validateOrganizerAccess(orgId, googleId, List.of(OrganizerRole.OWNER, OrganizerRole.MANAGER));

        Page<Order> orders = orderRepository.findOrdersByOrganizationId(orgId, pageable);

        return orders.map(orderConverter::convertToDTO);
    }

    @Override
    public DashboardStatsDTO getDashboardStats(Long orgId, String googleId) {
        validateOrganizerAccess(orgId, googleId, List.of(OrganizerRole.OWNER, OrganizerRole.MANAGER));

        Double totalRevenue = orderRepository.calculateRevenueByOrganization(orgId);
        Long totalOrders = orderRepository.countOrdersByOrganizationId(orgId);
        Long totalTicketsSold = ticketRepository.countTicketsSoldByOrganizationId(orgId);
        Long totalEvents = organizationRepository.countEventsByOrganizationId(orgId);

        return DashboardStatsDTO.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .totalTicketsSold(totalTicketsSold != null ? totalTicketsSold : 0L)
                .totalEvents(totalEvents != null ? totalEvents : 0L)
                .build();
    }
}
