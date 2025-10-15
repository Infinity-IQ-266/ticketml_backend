package com.ticketml.specification;


import com.ticketml.common.entity.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Component
public class EventSpecification {

    public static Specification<Event> hasTitleLike(String title) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(title)) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasLocationLike(String location) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(location)) {
                return null;
            }
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasStartDateAfterOrEqual(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) {
                return null;
            }
            System.out.println("1");
            return cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
        };
    }

    public static Specification<Event> hasEndDateBeforeOrEqual(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get("endDate"), endDate);
        };
    }
}

