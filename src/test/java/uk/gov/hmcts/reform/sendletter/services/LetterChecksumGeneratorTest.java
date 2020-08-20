package uk.gov.hmcts.reform.sendletter.services;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.sendletter.model.in.Doc;
import uk.gov.hmcts.reform.sendletter.model.in.LetterWithPdfsAndNumberOfCopiesRequest;
import uk.gov.hmcts.reform.sendletter.model.in.LetterWithPdfsRequest;

import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class LetterChecksumGeneratorTest {
    private Supplier<LetterWithPdfsAndNumberOfCopiesRequest> letterSupplier =
        () -> new LetterWithPdfsAndNumberOfCopiesRequest(
            asList(
                    new Doc("foo".getBytes(),5),
                    new Doc("good".getBytes(),10)
            ),
            "print-job-1234",
            Map.of(
                    "doc_type", "my doc type",
                    "caseId", "123"
            )
        );

    private Supplier<LetterWithPdfsAndNumberOfCopiesRequest> secondLetterSupplier =
        () -> new LetterWithPdfsAndNumberOfCopiesRequest(
            asList(
                    new Doc("bar".getBytes(),4),
                    new Doc("day".getBytes(),4)
            ),
            "print-job-1234",
            Map.of(
                    "doc_type", "my doc type",
                    "caseId", "123"
            )

        );

    @Test
    void should_return_same_md5_checksum_hex_for_same_letter_objects() {
        assertThat(LetterChecksumGenerator.generateChecksum(letterSupplier.get()))
            .isEqualTo(LetterChecksumGenerator.generateChecksum(letterSupplier.get()));
    }

    @Test
    void should_return_same_md5_checksum_hex_for_same_letter_with_pdfs_objects() {

        Supplier<LetterWithPdfsRequest> letterSupplier =
            () -> new LetterWithPdfsRequest(
                asList(
                    "foo".getBytes(),
                    "bar".getBytes()
                ),
                "print-job-1234",
                ImmutableMap.of(
                    "doc_type", "my doc type",
                    "caseId", "123"
                )
            );

        LetterWithPdfsRequest letter1 = letterSupplier.get();
        LetterWithPdfsRequest letter2 = letterSupplier.get();

        assertThat(LetterChecksumGenerator.generateChecksum(letter1))
            .isEqualTo(LetterChecksumGenerator.generateChecksum(letter2));
    }

    @Test
    void should_return_different_md5_checksum_hex_for_different_letter_objects() {
        assertThat(LetterChecksumGenerator.generateChecksum(letterSupplier.get()))
            .isNotEqualTo(LetterChecksumGenerator.generateChecksum(secondLetterSupplier.get()));
    }

    @Test
    void should_return_different_md5_checksum_hex_for_different_letter_with_pdf_objects() {

        LetterWithPdfsRequest letter1 = new LetterWithPdfsRequest(
            asList(
                "foo".getBytes(),
                "bar".getBytes()
            ),
            "print-job-1234",
            ImmutableMap.of(
                "doc_type", "my doc type",
                "caseId", "123"
            )
        );

        LetterWithPdfsRequest letter2 = new LetterWithPdfsRequest(
            asList(
                "foo".getBytes(),
                "bar!".getBytes()
            ),
            "print-job-1234",
            ImmutableMap.of(
                "doc_type", "my doc type",
                "caseId", "123"
            )
        );

        assertThat(LetterChecksumGenerator.generateChecksum(letter1))
            .isNotEqualTo(LetterChecksumGenerator.generateChecksum(letter2));
    }
}
