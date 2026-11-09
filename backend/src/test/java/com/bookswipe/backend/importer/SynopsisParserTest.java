package com.bookswipe.backend.importer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class SynopsisParserTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    // ---------- extract: the two valid shapes ----------

    @Test
    void plainTextIsReturned() {
        JsonNode node = mapper.readTree("\"A plain description.\"");

        assertThat(SynopsisParser.extract(node)).contains("A plain description.");
    }

    @Test
    void typedObjectReturnsItsValue() {
        JsonNode node =
                mapper.readTree("{\"type\":\"/type/text\",\"value\":\"A typed description.\"}");

        assertThat(SynopsisParser.extract(node)).contains("A typed description.");
    }

    @Test
    void lineBreaksAreNormalizedWhenExtracting() {
        JsonNode node = mapper.readTree("{\"value\":\"First\\r\\n\\r\\nSecond\"}");

        assertThat(SynopsisParser.extract(node)).contains("First\n\nSecond");
    }

    // ---------- extract: nothing usable ----------

    @Test
    void nullReferenceIsEmpty() {
        assertThat(SynopsisParser.extract(null)).isEmpty();
    }

    @Test
    void absentFieldIsEmpty() {
        JsonNode missing = mapper.readTree("{}").path("description");

        assertThat(SynopsisParser.extract(missing)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "null", // explicit JSON null
                    "42", // a number
                    "[]", // a list
                    "{}", // an object without value
                    "{\"type\":\"/type/text\"}", // typed object, still no value
                    "{\"value\":5}" // value is not text
            })
    void unusableShapesAreEmpty(String json) {
        JsonNode node = mapper.readTree(json);

        assertThat(SynopsisParser.extract(node)).isEmpty();
    }

    // ---------- normalize ----------

    @Test
    void windowsLineBreaksBecomeUnixOnes() {
        assertThat(SynopsisParser.normalize("a\r\n\r\nb")).isEqualTo("a\n\nb");
    }

    @Test
    void lonelyCarriageReturnBecomesALineBreak() {
        assertThat(SynopsisParser.normalize("a\rb")).isEqualTo("a\nb");
    }

    @Test
    void longRunsOfLineBreaksCollapseToTwo() {
        assertThat(SynopsisParser.normalize("a\n\n\n\n\nb")).isEqualTo("a\n\nb");
    }

    @Test
    void surroundingWhitespaceIsTrimmed() {
        assertThat(SynopsisParser.normalize("  \n a \n  ")).isEqualTo("a");
    }

    @Test
    void aSingleParagraphBreakIsKept() {
        assertThat(SynopsisParser.normalize("a\n\nb")).isEqualTo("a\n\nb");
    }
}
