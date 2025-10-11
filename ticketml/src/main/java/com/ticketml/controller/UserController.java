package com.ticketml.controller;

import com.ticketml.common.enums.TicketStatus;
import com.ticketml.response.Response;
import com.ticketml.services.OrderService;
import com.ticketml.services.TicketService;
import com.ticketml.services.UserService;
import com.ticketml.util.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final TicketService ticketService;
    private final OrderService orderService;

    public UserController(UserService userService, TicketService ticketService, OrderService orderService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.orderService = orderService;
    }

    @GetMapping("/me")
    public Response getCurrentUser(){
        String googleId = SecurityUtil.getGoogleId();
        return new Response(userService.findMe(googleId));
    }

    @GetMapping("/me/orders")
    public Response getMyOrderHistory() {
        String googleId = SecurityUtil.getGoogleId();
        return new Response(orderService.getMyOrderHistory(googleId));
    }

    @GetMapping("/me/tickets")
    public Response getTickets(
            @RequestParam(name = "status", required = false, defaultValue = "ACTIVE") TicketStatus status
    ){
        String  googleId = SecurityUtil.getGoogleId();
        return new Response(ticketService.getMyTickets(googleId, status));
    }

}
