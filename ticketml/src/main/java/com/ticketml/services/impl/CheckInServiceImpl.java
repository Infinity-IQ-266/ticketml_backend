package com.ticketml.services.impl;

import com.ticketml.common.dto.checkIn.CheckInRequestDTO;
import com.ticketml.common.dto.checkIn.CheckInResponseDTO;
import com.ticketml.common.dto.ticket.TicketDetailResponseDTO;
import com.ticketml.common.entity.Event;
import com.ticketml.common.entity.Ticket;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.CheckInStatus;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.common.enums.TicketStatus;
import com.ticketml.exception.ForbiddenException;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.EventRepository;
import com.ticketml.repository.OrganizerMembershipRepository;
import com.ticketml.repository.TicketRepository;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.CheckInService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CheckInServiceImpl implements CheckInService {
    private final UserRepository userRepository;
    private final OrganizerMembershipRepository membershipRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public CheckInServiceImpl(UserRepository userRepository, OrganizerMembershipRepository membershipRepository, EventRepository eventRepository, TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public CheckInResponseDTO processCheckIn(Long eventId, CheckInRequestDTO requestDTO, String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND, "Event not found"));

        if (!membershipRepository.existsByUserAndOrganizationId(currentUser, event.getOrganization().getId())) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY, "You are not a member of this event's organization.");
        }
        Ticket ticket = ticketRepository.findByQrCode(requestDTO.getQrCode())
                .orElse(null);

        if (ticket == null) {
            return CheckInResponseDTO.builder().status(CheckInStatus.valueOf("INVALID_TICKET")).message("The ticket is invalid.").build();
        }
        if (!Objects.equals(ticket.getTicketType().getEvent().getId(), eventId)) {
            return CheckInResponseDTO.builder().status(CheckInStatus.valueOf("WRONG_EVENT")).message("This ticket does not belong to this event.").build();
        }
        if (ticket.isCheckedIn()) {
            return CheckInResponseDTO.builder().status(CheckInStatus.valueOf("ALREADY_CHECKED_IN")).message("The ticket has already been checked in.").build();
        }

        ticket.setCheckedIn(true);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);

        TicketDetailResponseDTO responseDTO = new  TicketDetailResponseDTO();

        String fullName = ticket.getOrderItem().getOrder().getUser().getFirstName() + " " + ticket.getOrderItem().getOrder().getUser().getLastName();
        responseDTO.setUserName(fullName);
        responseDTO.setTicketType(ticket.getTicketType().getType());
        responseDTO.setCheckInTime(ticket.getUpdatedAt());

        return CheckInResponseDTO.builder()
                .status(CheckInStatus.valueOf("SUCCESS"))
                .message("Check-in thành công!")
                .ticketDetails(responseDTO)
                .build();
    }
}
