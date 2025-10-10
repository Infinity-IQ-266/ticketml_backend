package com.ticketml.services.impl;

import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import com.ticketml.common.dto.ticketType.TicketTypeResponseDTO;
import com.ticketml.common.entity.TicketType;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.common.enums.OrganizerRole;
import com.ticketml.converter.TicketConverter;
import com.ticketml.exception.ForbiddenException;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.OrganizerMembershipRepository;
import com.ticketml.repository.TicketTypeRepository;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.TicketTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketConverter ticketConverter;
    private final UserRepository userRepository;
    private final OrganizerMembershipRepository membershipRepository;

    public TicketTypeServiceImpl(TicketTypeRepository ticketTypeRepository, TicketConverter ticketConverter, UserRepository userRepository, OrganizerMembershipRepository membershipRepository) {
        this.ticketTypeRepository = ticketTypeRepository;
        this.ticketConverter = ticketConverter;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    @Transactional
    public TicketTypeResponseDTO updateTicketType(Long ticketTypeId, TicketTypeRequestDTO requestDTO, String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));

        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.TICKET_TYPE_NOT_FOUND, "Ticket type not found"));

        Long organizationId = ticketType.getEvent().getOrganization().getId();
        if (!membershipRepository.existsByUserAndOrganizationIdAndRoleInOrg(currentUser, organizationId, OrganizerRole.OWNER)) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY, "Only the OWNER can update ticket types.");
        }
        if(requestDTO.getType() != null) {
            ticketType.setType(requestDTO.getType());
        }
        if(requestDTO.getPrice() != null) {
            ticketType.setPrice(requestDTO.getPrice());
        }
        if(requestDTO.getTotalQuantity() != null) {
            ticketType.setTotalQuantity(requestDTO.getTotalQuantity());
        }
        TicketType updatedTicketType = ticketTypeRepository.save(ticketType);
        return ticketConverter.convertToResponseDTO(updatedTicketType);
    }
}
