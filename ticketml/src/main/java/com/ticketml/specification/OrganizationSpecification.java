package com.ticketml.specification;

import com.ticketml.common.entity.Organization;
import com.ticketml.common.enums.OrganizationStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class OrganizationSpecification {

    public static Specification<Organization> hasNameLike(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Organization> hasEmailLike(String email) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(email)) {
                return null;
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<Organization> hasStatus(OrganizationStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Organization> hasCreatedAfterOrEqual(LocalDate createdAfter) {
        return (root, query, cb) -> {
            if (createdAfter == null) {
                return null;
            }
            LocalDateTime startOfDay = createdAfter.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), startOfDay);
        };
    }

    public static Specification<Organization> hasCreatedBeforeOrEqual(LocalDate createdBefore) {
        return (root, query, cb) -> {
            if (createdBefore == null) {
                return null;
            }
            LocalDateTime endOfDay = createdBefore.atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("createdAt"), endOfDay);
        };
    }
}
