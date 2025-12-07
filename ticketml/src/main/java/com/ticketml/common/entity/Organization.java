package com.ticketml.common.entity;

import com.ticketml.common.enums.OrganizationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "organizations")
@EqualsAndHashCode(callSuper = true)
public class Organization extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String address;

    private String logoUrl;

    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private OrganizationStatus status;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizerMembership> memberships = new HashSet<>();
}