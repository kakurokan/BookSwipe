package com.bookswipe.backend.common.Exception;

public class AlreadySwipedException extends ApiException {
    public AlreadySwipedException(long swipeId) {
        super("Swipe: " + swipeId + " was already swiped", ErrorCode.ALREADY_SWIPED);
    }
}
