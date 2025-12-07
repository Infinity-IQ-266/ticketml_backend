package com.ticketml.common.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class FunctionResponseDTO {
    private String name;
    private Map<String, Object> response;
}
