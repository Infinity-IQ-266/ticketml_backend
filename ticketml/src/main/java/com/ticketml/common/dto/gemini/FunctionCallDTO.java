package com.ticketml.common.dto.gemini;

import lombok.Data;

import java.util.Map;

@Data
public class FunctionCallDTO {
    private String name;
    private Map<String, Object> args;
}
