package com.ticketml.services.impl;

import com.ticketml.common.dto.user.UserResponseDto;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.converter.UserConverter;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserServiceImpl(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Override
    public UserResponseDto findMe(String googleId) {
        Optional<User> userOptional = userRepository.findByGoogleId(googleId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found");
        }
        return userConverter.convertToDTO(userOptional.get());
    }
}
