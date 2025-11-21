package com.ticketml.common.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class GeminiRequestDTO {
    private List<ContentDTO> contents;
}