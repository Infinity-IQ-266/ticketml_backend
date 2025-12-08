package com.ticketml.repository;

import com.ticketml.common.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,Long>, JpaSpecificationExecutor<Organization> {
    @Query("SELECT COUNT(e) FROM Event e WHERE e.organization.id = :orgId")
    Long countEventsByOrganizationId(@Param("orgId") Long orgId);
}
