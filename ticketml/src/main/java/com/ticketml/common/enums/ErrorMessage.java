package com.ticketml.common.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    USER_NOT_FOUND(404, 1001, "User not found"),
    INTERNAL_ERROR(500, 9999, "Internal server error"),
    USERNAME_ALREADY_EXISTS(400, 1003, "Username is already in use"),
    INVALID_USERNAME_PASSWORD(400, 1005, "Invalid username or password"),
    FORBIDDEN_AUTHORITY(403, 1006, "Forbidden authority"),
    ORGANIZATION_NOT_FOUND(404,1007 ,"Organization not found" ),
    EVENT_NOT_FOUND(404, 1008, "Event not found"),
    TICKET_TYPE_NOT_FOUND(404, 1009, "Ticket type not found"),
    QUANTITY_NOT_ENOUGH(400, 1010, "Quantity not enough");

    private final int httpStatus;
    private final int code;
    private final String message;

    ErrorMessage(int httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}

