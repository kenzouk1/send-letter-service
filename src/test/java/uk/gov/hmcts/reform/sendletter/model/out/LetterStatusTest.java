package uk.gov.hmcts.reform.sendletter.model.out;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
class LetterStatusTest {
    @Autowired
    private JacksonTester<LetterStatus> json;

    @Test
    void testAdditionalDataPresent() throws IOException {
        UUID uuid = UUID.randomUUID();
        Map<String, Object> additionalData = Map.of("reference", "ABD-123-WAZ", "count", 10, "additionInfo", "present");
        LetterStatus letterStatus = new LetterStatus(uuid, "TEST", "abc",
                ZonedDateTime.now(), ZonedDateTime.now().plusHours(1),
                ZonedDateTime.now().plusHours(2), additionalData,1);
        JsonContent<LetterStatus> jsonContent = this.json.write(letterStatus);
        assertThat(jsonContent).hasJsonPathStringValue("$.id")
                .hasJsonPath("$.additional_data")
                .hasJsonPath("$.copies")
                .hasJsonPathValue("[?(@.copies==1)]");
    }

    @Test
    void testWithEmptyAdditionalData() throws IOException {
        UUID uuid = UUID.randomUUID();
        LetterStatus letterStatus = new LetterStatus(uuid, "TEST", "abc",
                ZonedDateTime.now(), ZonedDateTime.now().plusHours(1),
                ZonedDateTime.now().plusHours(2), Collections.emptyMap(),10);
        JsonContent<LetterStatus> jsonContent = this.json.write(letterStatus);
        assertThat(jsonContent).hasJsonPathStringValue("$.id")
                .hasJsonPath("$.additional_data")
                .hasJsonPath("$.copies")
                .hasJsonPathValue("[?(@.copies==10)]");
    }

    @Test
    void testWithNullAdditionalDataPresent() throws IOException {
        UUID uuid = UUID.randomUUID();
        LetterStatus letterStatus = new LetterStatus(uuid, "TEST", "abc",
                ZonedDateTime.now(), ZonedDateTime.now().plusHours(1),
                ZonedDateTime.now().plusHours(2), null, 12);
        JsonContent<LetterStatus> jsonContent = this.json.write(letterStatus);
        assertThat(jsonContent).hasJsonPathStringValue("$.id")
                .doesNotHaveJsonPath("$.additional_data")
                .hasJsonPath("$.copies")
                .hasJsonPathValue("[?(@.copies==12)]");
    }
}