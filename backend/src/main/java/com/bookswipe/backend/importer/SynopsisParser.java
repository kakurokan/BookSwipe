package com.bookswipe.backend.importer;

import tools.jackson.databind.JsonNode;

import java.util.Optional;

final class SynopsisParser {
    private SynopsisParser() {
    }

    static Optional<String> extract(JsonNode description) {
        if (description == null || description.isNull() || description.isMissingNode()) {
            return Optional.empty();
        }
        if (description.isString()) {
            return Optional.of(normalize(description.asString()));
        }
        JsonNode value = description.path("value");
        if (value.isString()) {
            return Optional.of(normalize(value.asString()));
        }
        return Optional.empty();
    }

    static String normalize(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n').replaceAll("\n{3,}", "\n\n").strip();
    }
}
