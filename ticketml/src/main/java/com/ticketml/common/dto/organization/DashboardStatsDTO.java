package com.ticketml.common.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Double totalRevenue;
    private Long totalOrders;
    private Long totalTicketsSold;
    private Long totalEvents;
}
