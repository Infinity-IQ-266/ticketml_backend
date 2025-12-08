package com.ticketml.common.dto.event;

import com.ticketml.common.dto.ticketType.TicketTypeRequestDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class EventCreateRequestDTO {
    @NotBlank
    String title;
    String description;
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;
    @NotBlank
    String location;

    private MultipartFile bannerImage;

    @NotEmpty
    private List<TicketTypeRequestDTO> ticketTypes;
}
