package com.ticketml.repository;

import com.ticketml.common.entity.Ticket;
import com.ticketml.common.enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @EntityGraph(value = "Ticket.withDetails")
    Optional<Ticket> findByQrCode(String qrCode);

    @EntityGraph(value = "Ticket.withDetails")
    @Query("SELECT t FROM Ticket t JOIN t.orderItem oi JOIN oi.order o WHERE o.user.id = :userId AND t.status = :status")
    List<Ticket> findTicketsByUserAndStatus(Long userId, TicketStatus status);
}
