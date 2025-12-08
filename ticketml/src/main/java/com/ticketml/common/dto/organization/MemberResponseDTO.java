package com.ticketml.common.dto.organization;

import com.ticketml.common.enums.MembershipStatus;
import com.ticketml.common.enums.OrganizerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {
    private Long userId;
    private String email;
    private String fullName;
    private OrganizerRole role;
    private MembershipStatus status;
}
