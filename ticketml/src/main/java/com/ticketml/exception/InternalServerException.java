package com.ticketml.exception;

import com.ticketml.common.enums.ErrorMessage;

public class InternalServerException extends GenericException {
    public InternalServerException(ErrorMessage error) {
        super(error.getCode(), error.getMessage(), error.getHttpStatus());
    }
}
