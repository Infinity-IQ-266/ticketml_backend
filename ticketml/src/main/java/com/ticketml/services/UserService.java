package com.ticketml.services;

import com.ticketml.common.dto.user.UserResponseDto;
import com.ticketml.common.dto.user.UserUpdateDTO;


public interface UserService {
    UserResponseDto findMe(String googleId);

    UserResponseDto updateProfile(String googleId, UserUpdateDTO request);
}
