package com.bookswipe.backend.importer;

enum SkipReason {
    NO_TITLE,
    NO_AUTHOR,
    LOW_POPULARITY,
    ALREADY_IMPORTED,
    NO_DESCRIPTION,
    SHORT_DESCRIPTION
}
