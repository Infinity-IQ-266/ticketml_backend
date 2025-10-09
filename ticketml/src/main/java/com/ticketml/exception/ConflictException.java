package com.ticketml.exception;


import com.ticketml.common.enums.ErrorMessage;

public class ConflictException extends GenericException {
    public ConflictException(ErrorMessage error) {
        super(error.getCode(), error.getMessage(), error.getHttpStatus());
    }
}
