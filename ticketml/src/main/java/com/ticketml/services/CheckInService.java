package com.ticketml.services;

import com.ticketml.common.dto.checkIn.CheckInRequestDTO;
import com.ticketml.common.dto.checkIn.CheckInResponseDTO;
import jakarta.validation.Valid;

public interface CheckInService {
    CheckInResponseDTO processCheckIn(Long eventId, @Valid CheckInRequestDTO requestDTO, String googleId);
}
