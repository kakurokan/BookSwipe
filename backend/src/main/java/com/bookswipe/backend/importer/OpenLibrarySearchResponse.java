package com.bookswipe.backend.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record OpenLibrarySearchResponse(List<Doc> docs) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Doc(
            String key,
            String title,
            @JsonProperty("author_name") List<String> authorNames,
            @JsonProperty("author_key") List<String> authorKeys,
            @JsonProperty("first_publish_year") Integer firstPublishYear,
            @JsonProperty("cover_i") Integer coverId,
            @JsonProperty("number_of_pages_median") Integer pageCount,
            @JsonProperty("want_to_read_count") Integer wantToReadCount) {
        String workId() {
            return key == null ? null : key.substring(key.lastIndexOf('/') + 1);
        }
    }
}
