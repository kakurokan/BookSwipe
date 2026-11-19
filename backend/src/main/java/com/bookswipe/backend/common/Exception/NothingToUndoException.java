package com.bookswipe.backend.common.Exception;

public class NothingToUndoException extends ApiException {
    public NothingToUndoException(String message, ErrorCode errorCode) {
        super("No swipe to undo", ErrorCode.NOTHING_TO_UNDO);
    }
}
