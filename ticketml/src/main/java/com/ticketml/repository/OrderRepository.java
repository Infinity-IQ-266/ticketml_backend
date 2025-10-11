package com.ticketml.repository;

import com.ticketml.common.entity.Order;
import com.ticketml.common.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(value = "Order.withItemsAndEventDetails")
    List<Order> findByUserOrderByCreatedAtDesc(User currentUser);
}
