package com.bookswipe.backend.importer;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("import")
class OpenLibraryClient {
    private static final String FIELDS =
            "key,title,author_name,author_key,first_publish_year,"
                    + "cover_i,number_of_pages_median,want_to_read_count";

    private final RestClient restClient;
    private final long pauseMs;

    OpenLibraryClient(ImporterProperties props) {
        this.pauseMs = props.pauseMs();
        this.restClient =
                RestClient.builder()
                        .baseUrl("https://openlibrary.org")
                        .defaultHeader(
                                HttpHeaders.USER_AGENT, "BookSwipe/1.0 (" + props.contact() + ")")
                        .build();
    }

    OpenLibrarySearchResponse searchBySubject(String subject, int page, int limit) {
        pause();
        return restClient
                .get()
                .uri(uri ->
                        uri.path("/search.json")
                                .queryParam("q", "subject:" + subject + " language:eng")
                                .queryParam("fields", FIELDS)
                                .queryParam("sort", "want_to_read")
                                .queryParam("limit", limit)
                                .queryParam("page", page)
                                .build())
                .retrieve()
                .body(OpenLibrarySearchResponse.class);
    }

    OpenLibraryWork fetchWork(String workId) {
        pause();
        return restClient
                .get()
                .uri("/works/{id}.json", workId)
                .retrieve()
                .body(OpenLibraryWork.class);
    }

    private void pause() {
        try {
            Thread.sleep(pauseMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting between requests", e);
        }
    }
}
