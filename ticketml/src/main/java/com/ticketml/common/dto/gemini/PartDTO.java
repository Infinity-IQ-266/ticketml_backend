package com.ticketml.common.dto.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartDTO {
    private String text;
    private FunctionCallDTO functionCall;
    private FunctionResponseDTO functionResponse;

    public PartDTO(String text) {
        this.text = text;
    }
}
