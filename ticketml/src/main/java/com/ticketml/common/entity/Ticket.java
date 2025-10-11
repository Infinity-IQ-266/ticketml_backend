package com.ticketml.common.entity;

import com.ticketml.common.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")
@EqualsAndHashCode(callSuper = true)
@NamedEntityGraph(
        name = "Ticket.withDetails",
        attributeNodes = {
                @NamedAttributeNode(value = "orderItem", subgraph = "orderItem-details"),
                @NamedAttributeNode(value = "ticketType", subgraph = "ticketType-details")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "orderItem-details",
                        attributeNodes = {
                                @NamedAttributeNode("order")
                        }
                ),
                @NamedSubgraph(
                        name = "ticketType-details",
                        attributeNodes = {
                                @NamedAttributeNode("event")
                        }
                )
        }
)
public class Ticket extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String qrCode;

    private boolean checkedIn = false;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;
}