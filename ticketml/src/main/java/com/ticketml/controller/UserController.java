package com.ticketml.controller;

import com.ticketml.response.Response;
import com.ticketml.services.OrganizationService;
import com.ticketml.services.UserService;
import com.ticketml.util.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, OrganizationService organizationService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Response getCurrentUser(){
        String googleId = SecurityUtil.getGoogleId();
        System.out.println("Google Id: " + googleId);
        return new Response(userService.findMe(googleId));
    }

}
