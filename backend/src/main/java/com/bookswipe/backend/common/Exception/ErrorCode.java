package com.bookswipe.backend.common.Exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    EMAIL_ALREADY_USED(HttpStatus.CONFLICT),
    SWIPE_NOT_FOUND(HttpStatus.NOT_FOUND),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_SWIPED(HttpStatus.CONFLICT),
    NOTHING_TO_UNDO(HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    ErrorCode(HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public HttpStatus status() {
        return status;
    }
}
