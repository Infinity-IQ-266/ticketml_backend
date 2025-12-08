package com.ticketml.common.dto.user;

import lombok.Data;

@Data
public class UserResponseDto {

    private String fullName;

    private String email;

    private String imageUrl;

    private String address;

    private String phoneNumber;

}
