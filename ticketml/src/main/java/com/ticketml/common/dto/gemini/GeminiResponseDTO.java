package com.ticketml.common.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Bỏ qua các trường không cần thiết trong response
public class GeminiResponseDTO {
    private List<CandidateDTO> candidates;
}
