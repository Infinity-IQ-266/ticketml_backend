package com.ticketml.common.entity;

import com.ticketml.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NamedEntityGraph(
        name = "Order.withItemsAndEventDetails",
        attributeNodes = {
                @NamedAttributeNode(value = "orderItems", subgraph = "orderItems-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "orderItems-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "ticketType", subgraph = "ticketType-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "ticketType-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("event")
                        }
                )
        }
)
public class Order extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}