package com.ticketml.repository;

import com.ticketml.common.entity.Order;
import com.ticketml.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(value = "Order.withItemsAndEventDetails")
    List<Order> findByUserOrderByCreatedAtDesc(User currentUser);

   /* @Query("SELECT o FROM Order o JOIN o.orderItems oi JOIN oi.ticketType tt JOIN tt.event e " +
            "WHERE e.organization.id = :orgId GROUP BY o.id ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByOrganizationId(@Param("orgId") Long orgId);

    @Query("SELECT SUM(o.totalPrice) FROM Order o JOIN o.orderItems oi JOIN oi.ticketType tt JOIN tt.event e " +
            "WHERE e.organization.id = :orgId AND o.status = 'CONFIRMED'")
    Double calculateRevenueByOrganization(@Param("orgId") Long orgId);*/
}
