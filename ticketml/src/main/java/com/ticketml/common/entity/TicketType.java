package com.ticketml.common.entity;

import com.ticketml.common.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket_types")
@EqualsAndHashCode(callSuper = true)
public class TicketType extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private double price;

    private int totalQuantity;

    private int remainingQuantity;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}