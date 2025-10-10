package com.ticketml.repository;

import com.ticketml.common.entity.OrganizerMembership;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.OrganizerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizerMembershipRepository extends JpaRepository<OrganizerMembership, Long> {

    List<OrganizerMembership> findByUser(User user);

    Optional<OrganizerMembership> findByUserAndOrganizationId(User user, Long organizationId);

    boolean existsByUserAndOrganizationId(User user, Long organizationId);

    boolean existsByUserAndOrganizationIdAndRoleInOrg(User user, Long organizationId, OrganizerRole role);
}