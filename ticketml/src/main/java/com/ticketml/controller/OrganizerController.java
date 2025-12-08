package com.ticketml.controller;

import com.ticketml.common.dto.checkIn.CheckInRequestDTO;
import com.ticketml.common.dto.event.EventCreateRequestDTO;
import com.ticketml.common.dto.event.EventUpdateRequestDTO;
import com.ticketml.common.dto.organization.MemberRequestDTO;
import com.ticketml.common.dto.organization.OrganizationRequestDTO;
import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import com.ticketml.response.Response;
import com.ticketml.services.*;
import com.ticketml.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/organizer")
@PreAuthorize("isAuthenticated()")
@Validated
public class OrganizerController {
    private final OrganizationService organizationService;
    private final EventService eventService;
    private final TicketTypeService ticketTypeService;
    private final CheckInService checkInService;

    public OrganizerController(OrganizationService organizationService,
                               EventService eventService,
                               TicketTypeService ticketTypeService,
                               CheckInService checkInService) {
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
    public Response createOrganization(@ModelAttribute @Valid OrganizationRequestDTO request) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(organizationService.createOrganization(googleId, request));
    }

    @PutMapping(value = "/organizations/{orgId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response updateOrganization(
            @PathVariable Long orgId,
            @ModelAttribute @Valid OrganizationRequestDTO request) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(organizationService.updateOrganization(orgId, googleId, request));
    }

    @GetMapping("/organizations/{orgId}/members")
    public Response getMembers(@PathVariable Long orgId) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(organizationService.getMembers(orgId, googleId));
    }

    @PostMapping("/organizations/{orgId}/members")
    public Response addMember(@PathVariable Long orgId, @Valid @RequestBody MemberRequestDTO request) {
        String googleId = SecurityUtil.getGoogleId();
        organizationService.addMember(orgId, googleId, request);
        return new Response("Member added successfully");
    }

    @DeleteMapping("/organizations/{orgId}/members/{userId}")
    public Response removeMember(@PathVariable Long orgId, @PathVariable Long userId) {
        String googleId = SecurityUtil.getGoogleId();
        organizationService.removeMember(orgId, userId, googleId);
        return new Response("Member removed successfully");
    }

    @GetMapping("/organizations/{orgId}/orders")
    public Response getOrders(
            @PathVariable Long orgId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        String googleId = SecurityUtil.getGoogleId();
        Pageable pageable = PageRequest.of(page, size);
        return new Response(organizationService.getOrganizationOrders(orgId, googleId, pageable));
    }

    @GetMapping("/organizations/{orgId}/dashboard")
    public Response getDashboard(@PathVariable Long orgId) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(organizationService.getDashboardStats(orgId, googleId));
    }

    @GetMapping("/organizations/{orgId}/events")
    public Response getEventsByOrganization(@PathVariable Long orgId) {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(eventService.findByOrganizationId(orgId, googleId));
    }

    @PostMapping(value = "/organizations/{orgId}/events", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Response createEvent(
            @PathVariable Long orgId,
            @ModelAttribute @Valid EventCreateRequestDTO requestDTO) {

        String googleId = SecurityUtil.getGoogleId();
        return new Response(eventService.createEventWithTickets(orgId, requestDTO, googleId));
    }

    @PatchMapping(value = "/events/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response updateEvent(
            @PathVariable Long eventId,
            @ModelAttribute @Valid EventUpdateRequestDTO requestDTO) {

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
