package com.ticketml.common.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateDTO {
    private String fullName;

    private String phoneNumber;

    private String address;

    private MultipartFile avatar;
}