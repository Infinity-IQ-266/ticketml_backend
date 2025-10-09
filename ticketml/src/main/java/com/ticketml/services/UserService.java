package com.ticketml.services;

import com.ticketml.common.dto.UserResponseDto;


public interface UserService {
    UserResponseDto findMe(String googleId);
}
