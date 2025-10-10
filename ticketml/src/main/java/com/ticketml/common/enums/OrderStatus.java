package com.ticketml.common.enums;

public enum OrderStatus {
    PENDING,    // chờ thanh toán hoặc xác nhận
    CONFIRMED,  // đã xác nhận
    CANCELED,   // bị hủy
    FAILED,
    COMPLETED, USED        // vé đã được sử dụng
}
