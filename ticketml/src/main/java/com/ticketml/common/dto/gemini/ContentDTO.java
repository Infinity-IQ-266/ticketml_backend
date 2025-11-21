package com.ticketml.common.dto.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Không serialize trường null (ví dụ: role)
public class ContentDTO {
    private List<PartDTO> parts;
    private String role;
}
