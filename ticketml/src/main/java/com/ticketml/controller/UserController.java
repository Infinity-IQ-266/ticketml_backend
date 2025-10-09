package com.ticketml.controller;

import com.ticketml.response.Response;
import com.ticketml.services.UserService;
import com.ticketml.util.SecurityUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    Response getCurrentUser(){
        String googleId = SecurityUtil.getGoogleId();
        return new Response(userService.findMe(googleId));
    }
}
