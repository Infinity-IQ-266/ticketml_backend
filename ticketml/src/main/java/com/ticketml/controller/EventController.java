package com.ticketml.controller;

import com.ticketml.common.dto.event.EventSearchRequestDto;
import com.ticketml.common.enums.DirectionEnum;
import com.ticketml.response.Response;
import com.ticketml.services.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping()
    public Response getAllEvents(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") DirectionEnum direction,
            @RequestParam(name = "attribute", required = false, defaultValue = "createdAt") String attribute,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "location", required = false) String location
    ) {
        return new Response(eventService.searchEvent(
                EventSearchRequestDto
                        .builder()
                        .page(page)
                        .size(size)
                        .direction(direction)
                        .attribute(attribute)
                        .title(title)
                        .startDate(startDate)
                        .endDate(endDate)
                        .location(location)
                        .build()
        ));
    }
    @GetMapping("/{eventId}")
    public Response getEventById(@PathVariable("eventId") Long eventId) {
        return new Response(eventService.findEventById(eventId));
    }
}
