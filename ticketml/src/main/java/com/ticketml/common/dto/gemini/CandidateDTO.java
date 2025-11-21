package com.ticketml.common.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateDTO {
    private ContentDTO content;
}
