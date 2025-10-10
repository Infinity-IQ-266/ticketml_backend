package com.ticketml.repository;

import com.ticketml.common.entity.Ticket;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Optional<Ticket> findByQrCode(String qrCode);
}
