package uk.gov.hmcts.reform.sendletter.model.in;

import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(SpringExtension.class)
@JsonTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LetterWithPdfsAndNumberOfCopiesRequestTest {
    @Autowired
    private JacksonTester<LetterWithPdfsAndNumberOfCopiesRequest> json;

    private String validJson;
    private LetterWithPdfsAndNumberOfCopiesRequest letter;

    @BeforeAll
    void setUp() throws Exception {
        this.validJson = Resources.toString(
                getResource("controller/letter/v3/valid_letter_additionaldata.json"), UTF_8);
        letter = new LetterWithPdfsAndNumberOfCopiesRequest(
                asList(
                        new Doc("hello".getBytes(), 1),
                        new Doc("world".getBytes(), 10)
                ),
                "typeA",
                Map.of(
                        "doc_type", "my doc type",
                        "caseId", "123"
                )
        );
    }

    @Test
    public void testSerialize() throws Exception {
        // Assert against a `.json` file in the same package as the test
        assertThat(this.json.write(letter)).isEqualToJson(validJson, JSONCompareMode.STRICT);
    }

    @Test
    public void testDeserialize() throws Exception {
        assertThat(this.json.parseObject(validJson).documents).extracting("content", "copies")
                .contains(tuple(letter.documents.get(0).content, letter.documents.get(0).copies),
                        tuple(letter.documents.get(1).content, letter.documents.get(1).copies));
        assertThat(this.json.parseObject(validJson).type).contains(letter.type);
        assertThat(this.json.parseObject(validJson).additionalData).containsExactlyEntriesOf(letter.additionalData);
    }




}