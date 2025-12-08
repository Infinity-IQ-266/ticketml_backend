package com.ticketml.common.dto.event;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class EventUpdateRequestDTO {
    String title;

    String description;

    String location;

    LocalDate startDate;

    LocalDate endDate;

    private MultipartFile bannerImage;
}
