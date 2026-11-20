package com.bookswipe.backend.common.Exception;

public class InvalidRequestException extends ApiException {

    public InvalidRequestException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }
}
