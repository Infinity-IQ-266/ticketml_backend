package com.ticketml.converter;

import com.ticketml.common.dto.UserResponseDto;
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
        return modelMapper.map(entity, UserResponseDto.class);
    }

    @Override
    public User convertToEntity(UserResponseDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
