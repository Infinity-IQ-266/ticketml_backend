package com.ticketml.controller;

import com.ticketml.common.dto.checkIn.CheckInRequestDTO;
import com.ticketml.common.dto.event.EventCreateRequestDTO;
import com.ticketml.common.dto.event.EventUpdateRequestDTO;
import com.ticketml.common.dto.organization.OrganizationRequestDTO;
import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import com.ticketml.response.Response;
import com.ticketml.services.EventService;
import com.ticketml.services.OrganizationService;
import com.ticketml.services.TicketTypeService;
import com.ticketml.services.CheckInService;
import com.ticketml.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/organizer")
@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
@Validated
public class OrganizerController {
    private final OrganizationService organizationService;
    private final EventService eventService;
    private final TicketTypeService ticketTypeService;
    private final CheckInService checkInService;

    public OrganizerController(OrganizationService organizationService, EventService eventService, TicketTypeService ticketTypeService, CheckInService checkInService) {
        this.organizationService = organizationService;
        this.eventService = eventService;
        this.ticketTypeService = ticketTypeService;
        this.checkInService = checkInService;
    }

    @GetMapping("/organizations")
    public Response getMyOrganizations() {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(organizationService.findOrganizationsByCurrentUser(googleId));
    }

    @PostMapping(value = "/organizations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response createOrganization(@ModelAttribute OrganizationRequestDTO request) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(organizationService.createOrganization(googleId, request));
    }

    @PutMapping(value = "/organizations/{orgId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response updateOrganization(
            @PathVariable Long orgId,
            @ModelAttribute OrganizationRequestDTO request) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(organizationService.updateOrganization(orgId, googleId, request));
    }

    @GetMapping("/organizations/{orgId}/events")
    public Response getEventsByOrganization(@PathVariable Long orgId) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(eventService.findByOrganizationId(orgId, googleId));
    }

    @PostMapping("/organizations/{orgId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Response createEvent(@PathVariable Long orgId, @Valid @RequestBody EventCreateRequestDTO requestDTO) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(eventService.createEventWithTickets(orgId, requestDTO, googleId));
    }

    @PatchMapping("/events/{eventId}")
    public Response updateEvent(@PathVariable Long eventId, @Valid @RequestBody EventUpdateRequestDTO requestDTO) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(eventService.updateEvent(eventId, requestDTO, googleId));
    }

    @PostMapping("/events/{eventId}/ticketTypes")
    @ResponseStatus(HttpStatus.CREATED)
    public Response addTicketTypeToEvent(@PathVariable Long eventId, @Valid @RequestBody TicketTypeRequestDTO requestDTO) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(eventService.addTicketTypeToEvent(eventId, requestDTO, googleId));
    }

    @PatchMapping("/ticketTypes/{ticketTypeId}")
    public Response updateTicketType(@PathVariable Long ticketTypeId, @Valid @RequestBody TicketTypeRequestDTO requestDTO) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(ticketTypeService.updateTicketType(ticketTypeId, requestDTO, googleId));
    }

    @PostMapping("/events/{eventId}/checkIn")
    public Response checkInTicket(@PathVariable Long eventId, @Valid @RequestBody CheckInRequestDTO requestDTO) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(checkInService.processCheckIn(eventId, requestDTO, googleId));
    }
}
