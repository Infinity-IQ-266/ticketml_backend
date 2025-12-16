package com.ticketml.services.impl;

import com.ticketml.common.dto.event.*;
import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import com.ticketml.common.dto.ticketType.TicketTypeResponseDTO;
import com.ticketml.common.entity.Event;
import com.ticketml.common.entity.Organization;
import com.ticketml.common.entity.TicketType;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.common.enums.OrganizerRole;
import com.ticketml.converter.EventConverter;
import com.ticketml.converter.TicketTypeConverter;
import com.ticketml.exception.ForbiddenException;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.*;
import com.ticketml.services.EventService;
import com.ticketml.services.S3Service;
import com.ticketml.specification.EventSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final OrganizerMembershipRepository membershipRepository;
    private final EventRepository eventRepository;
    private final EventConverter eventConverter;
    private final OrganizationRepository organizationRepository;
    private final TicketTypeConverter ticketTypeConverter;
    private final TicketTypeRepository ticketTypeRepository;
    private final EventSpecification eventSpecification;
    private final S3Service s3Service;

    public EventServiceImpl(UserRepository userRepository, OrganizerMembershipRepository membershipRepository, EventRepository eventRepository, EventConverter eventConverter, OrganizationRepository organizationRepository, TicketTypeConverter ticketTypeConverter, TicketTypeRepository ticketTypeRepository, EventSpecification eventSpecification, S3Service s3Service) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.eventRepository = eventRepository;
        this.eventConverter = eventConverter;
        this.organizationRepository = organizationRepository;
        this.ticketTypeConverter = ticketTypeConverter;
        this.ticketTypeRepository = ticketTypeRepository;
        this.eventSpecification = eventSpecification;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDetailResponseDTO> findByOrganizationId(Long organizationId, String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));
        if (!membershipRepository.existsByUserAndOrganizationId(currentUser, organizationId)) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY, "You are not a member of this organization.");
        }
        return eventRepository.findByOrganizationId(organizationId)
                .stream()
                .map(eventConverter::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventDetailResponseDTO createEventWithTickets(Long organizationId, EventCreateRequestDTO requestDTO, String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));

        if (!membershipRepository.existsByUserAndOrganizationId(currentUser, organizationId)) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY, "You are not a member of this organization.");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ORGANIZATION_NOT_FOUND, "Organization not found"));

        Event event = eventConverter.convertRequestToEntity(requestDTO);

        event.setOrganization(organization);

        if (requestDTO.getBannerImage() != null && !requestDTO.getBannerImage().isEmpty()) {
            String imageUrl = s3Service.uploadFile(requestDTO.getBannerImage());
            event.setImageUrl(imageUrl);
        }

        List<TicketType> ticketTypes = requestDTO.getTicketTypes().stream()
                .map(ticketTypeConverter::convertRequestToEntity)
                .peek(ticketType -> ticketType.setEvent(event))
                .collect(Collectors.toList());

        event.setTicketTypes(ticketTypes);

        Event savedEvent = eventRepository.save(event);

        return eventConverter.convertToResponseDTO(savedEvent);
    }

    @Override
    @Transactional
    public EventDetailResponseDTO updateEvent(Long eventId, EventUpdateRequestDTO requestDTO, String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND, "Event not found"));
        if (!membershipRepository.existsByUserAndOrganizationIdAndRoleInOrg(currentUser, event.getOrganization().getId(), OrganizerRole.OWNER)) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY, "Only the OWNER can update the event.");
        }
        if (requestDTO.getTitle() != null) {
            event.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            event.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getLocation() != null) {
            event.setLocation(requestDTO.getLocation());
        }
        if (requestDTO.getStartDate() != null) {
            event.setStartDate(requestDTO.getStartDate());
        }
        if (requestDTO.getEndDate() != null) {
            event.setEndDate(requestDTO.getEndDate());
        }
        if (requestDTO.getBannerImage() != null && !requestDTO.getBannerImage().isEmpty()) {
            String imageUrl = s3Service.uploadFile(requestDTO.getBannerImage());
            event.setImageUrl(imageUrl);
        }
        eventRepository.save(event);
        return eventConverter.convertToResponseDTO(event);
    }

    @Override
    public TicketTypeResponseDTO addTicketTypeToEvent(Long eventId, TicketTypeRequestDTO requestDTO, String googleId) {
        User currentUser = userRepository.findByGoogleId(googleId).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND, "Event not found"));
        if (!membershipRepository.existsByUserAndOrganizationIdAndRoleInOrg(currentUser, event.getOrganization().getId(), OrganizerRole.OWNER)) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN_AUTHORITY, "Only the OWNER can add ticket types.");
        }
        TicketType ticketType = ticketTypeConverter.convertRequestToEntity(requestDTO);
        ticketType.setEvent(event);
        ticketTypeRepository.save(ticketType);
        return ticketTypeConverter.convertToResponseDTO(ticketType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDetailResponseDTO> searchEvent(EventSearchRequestDto request) {
        Specification<Event> spec = EventSpecification.hasTitleLike(request.getTitle())
                .and(EventSpecification.hasLocationLike(request.getLocation()))
                .and(EventSpecification.hasStartDateAfterOrEqual(request.getStartDate()))
                .and(EventSpecification.hasEndDateBeforeOrEqual(request.getEndDate()));
        Sort sortable = Sort.by(Sort.Direction.fromString(request.getDirection().name()), request.getAttribute());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sortable);

        Page<Event> events =  eventRepository.findAll(spec, pageable);

        return events.map(eventConverter::convertToResponseDTO);
    }

    @Override
    public EventDetailResponseDTO findEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND, "Event not found"));
        return eventConverter.convertToResponseDTO(event);
    }
}
