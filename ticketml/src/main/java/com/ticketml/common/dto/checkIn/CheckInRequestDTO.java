package com.ticketml.common.dto.checkIn;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckInRequestDTO {
    @NotBlank
    String qrCode;
}
