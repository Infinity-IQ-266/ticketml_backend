package com.ticketml.services;

import com.ticketml.common.dto.user.UserResponseDto;


public interface UserService {
    UserResponseDto findMe(String googleId);
}
