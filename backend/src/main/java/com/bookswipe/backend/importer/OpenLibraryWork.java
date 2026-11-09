package com.bookswipe.backend.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
record OpenLibraryWork(JsonNode description) {
}
