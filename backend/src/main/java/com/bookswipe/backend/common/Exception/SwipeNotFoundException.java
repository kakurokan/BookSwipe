package com.bookswipe.backend.common.Exception;

public class SwipeNotFoundException extends ApiException {
    public SwipeNotFoundException(long swipeId) {
        super("Swipe: " + swipeId + " not found", ErrorCode.SWIPE_NOT_FOUND);
    }
}
