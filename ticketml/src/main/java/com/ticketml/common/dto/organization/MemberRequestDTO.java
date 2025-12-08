package com.ticketml.common.dto.organization;
import com.ticketml.common.enums.OrganizerRole;
import lombok.Data;

@Data
public class MemberRequestDTO {
    private String email;
    private OrganizerRole role;
}