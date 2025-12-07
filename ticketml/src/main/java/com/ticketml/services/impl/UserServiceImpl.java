package com.ticketml.services.impl;

import com.ticketml.common.dto.user.UserResponseDto;
import com.ticketml.common.dto.user.UserUpdateDTO;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.ErrorMessage;
import com.ticketml.converter.UserConverter;
import com.ticketml.exception.NotFoundException;
import com.ticketml.repository.UserRepository;
import com.ticketml.services.S3Service;
import com.ticketml.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final S3Service s3Service;

    public UserServiceImpl(UserRepository userRepository, UserConverter userConverter, S3Service s3Service) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
        this.s3Service = s3Service;
    }

    @Override
    public UserResponseDto findMe(String googleId) {
        Optional<User> userOptional = userRepository.findByGoogleId(googleId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND, "User not found");
        }
        return userConverter.convertToDTO(userOptional.get());
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(String googleId, UserUpdateDTO request) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = s3Service.uploadFile(request.getAvatar());
            user.setImageUrl(avatarUrl);
        }

        User updatedUser = userRepository.save(user);
        return userConverter.convertToDTO(updatedUser);
    }
}
