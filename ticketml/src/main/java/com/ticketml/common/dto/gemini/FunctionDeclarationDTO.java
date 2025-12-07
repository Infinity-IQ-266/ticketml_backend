package com.ticketml.common.dto.gemini;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FunctionDeclarationDTO {
    private String name;
    private String description;
    private Map<String, Object> parameters;
}
