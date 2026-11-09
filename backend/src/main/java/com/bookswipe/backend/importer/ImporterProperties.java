package com.bookswipe.backend.importer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "bookswipe.importer")
public record ImporterProperties(
        @NotBlank String contact,
        @Positive long pauseMs,
        @Positive int minSynopsisLength,
        int minPopularity,
        @Positive int booksPerGenre) {
}