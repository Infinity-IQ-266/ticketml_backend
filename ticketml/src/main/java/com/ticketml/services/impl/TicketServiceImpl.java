package com.ticketml.services.impl;

import com.ticketml.common.dto.ticket.TicketResponseDTO;
import com.ticketml.common.entity.Ticket;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.common.enums.TicketStatus;
import com.ticketml.converter.TicketConverter;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.TicketRepository;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketConverter ticketConverter;

    public TicketServiceImpl(UserRepository userRepository, TicketRepository ticketRepository, TicketConverter ticketConverter) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.ticketConverter = ticketConverter;
    }

    @Override
    public List<TicketResponseDTO> getMyTickets(String googleId, TicketStatus status) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        List<Ticket> tickets;

        tickets = ticketRepository.findTicketsByUserAndStatus(currentUser.getId(), status);

        return tickets.stream()
                .map(ticketConverter::convertToDTO)
                .collect(Collectors.toList());
    }
}
