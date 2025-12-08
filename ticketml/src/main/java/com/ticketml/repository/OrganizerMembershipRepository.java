package com.ticketml.repository;

import com.ticketml.common.entity.OrganizerMembership;
import com.ticketml.common.entity.User;
import com.ticketml.common.enums.OrganizerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizerMembershipRepository extends JpaRepository<OrganizerMembership, Long> {
    List<OrganizerMembership> findByUser(User user);

    Optional<OrganizerMembership> findByUserAndOrganizationId(User user, Long organizationId);

    boolean existsByUserAndOrganizationId(User user, Long organizationId);

    boolean existsByUserAndOrganizationIdAndRoleInOrg(User user, Long organizationId, OrganizerRole role);


    @Query("SELECT om FROM OrganizerMembership om JOIN FETCH om.user WHERE om.organization.id = :orgId")
    List<OrganizerMembership> findByOrganizationIdWithUser(@Param("orgId") Long orgId);

    boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId);

    @Query("SELECT COUNT(om) FROM OrganizerMembership om WHERE om.organization.id = :orgId AND om.roleInOrg = :role")
    long countByOrganizationIdAndRole(@Param("orgId") Long orgId, @Param("role") OrganizerRole role);
}

