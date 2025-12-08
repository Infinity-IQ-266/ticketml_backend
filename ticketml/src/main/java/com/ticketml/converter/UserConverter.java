package com.ticketml.converter;

import com.ticketml.common.dto.user.UserResponseDto;
import com.ticketml.common.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("userConverter")
public class UserConverter extends SuperConverter<UserResponseDto, User> {


    private final ModelMapper modelMapper;

    public UserConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponseDto convertToDTO(User entity) {
        UserResponseDto dto = modelMapper.map(entity, UserResponseDto.class);
        dto.setRole(entity.getRole().name());
        return dto;
    }

    @Override
    public User convertToEntity(UserResponseDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
