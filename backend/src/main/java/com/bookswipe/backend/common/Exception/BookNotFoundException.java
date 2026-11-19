package com.bookswipe.backend.common.Exception;

public class BookNotFoundException extends ApiException {
    public BookNotFoundException(long bookId) {
        super("Book: " + bookId + " not found", ErrorCode.BOOK_NOT_FOUND);
    }
}
