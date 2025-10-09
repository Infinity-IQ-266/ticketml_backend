package com.ticketml.common.entity;

import com.ticketml.common.enums.MembershipStatus;
import com.ticketml.common.enums.OrganizerRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "organizer_memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "organization_id"}))
@EqualsAndHashCode(callSuper = true)
public class OrganizerMembership extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @ToString.Exclude
    private Organization organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_in_org")
    private OrganizerRole roleInOrg;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;
}