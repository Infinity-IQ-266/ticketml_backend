package com.ticketml.services;

import com.ticketml.common.entity.Order;
import com.ticketml.common.entity.Ticket;
import java.util.List;

public interface EmailService {
    void sendTicketEmail(String to, Order order, List<Ticket> tickets);
}