package com.ticketml.common.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ToolDTO {
    private List<FunctionDeclarationDTO> function_declarations;
}
