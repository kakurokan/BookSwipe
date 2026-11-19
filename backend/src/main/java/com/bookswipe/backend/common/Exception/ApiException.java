package com.bookswipe.backend.common.Exception;

import com.bookswipe.backend.common.ErrorResponse;

public abstract class ApiException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApiException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorResponse errorResponse() {
        return new ErrorResponse(errorCode.name(), this.getMessage());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
