package uk.gov.hmcts.reform.sendletter.services;

import com.google.common.io.ByteStreams;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.sendletter.model.in.Doc;
import uk.gov.hmcts.reform.sendletter.services.pdf.DuplexPreparator;
import uk.gov.hmcts.reform.sendletter.services.pdf.PdfCreator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.toByteArray;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PdfCreatorTest {

    @Mock private DuplexPreparator duplexPreparator;

    private PdfCreator pdfCreator;

    @BeforeEach
    void setUp() {
        pdfCreator = new PdfCreator(this.duplexPreparator);
    }

    @Test
    void should_handle_documents_with_number_of_copies_specified() throws Exception {
        // given
        byte[] test1Pdf = toByteArray(getResource("test1.pdf"));
        byte[] test2Pdf = toByteArray(getResource("test2.pdf"));

        given(duplexPreparator.prepare(test1Pdf)).willReturn(test1Pdf);
        given(duplexPreparator.prepare(test2Pdf)).willReturn(test2Pdf);

        Doc doc1 = new Doc(test1Pdf, 5);
        Doc doc2 = new Doc(test2Pdf, 10);

        // when
        byte[] result = pdfCreator.createFromBase64PdfWithCopies(asList(doc1, doc2));

        // then
        verify(duplexPreparator, times(1)).prepare(eq(doc1.content));
        verify(duplexPreparator, times(1)).prepare(eq(doc2.content));

        try (PDDocument doc = PDDocument.load(result)) {
            assertThat(doc.getNumberOfPages()).isEqualTo(doc1.copies + doc2.copies);
        }
    }

    @Test
    void should_return_a_merged_pdf_when_multiple_documents_are_passed() throws Exception {
        byte[] test1Pdf = toByteArray(getResource("test1.pdf"));
        byte[] test2Pdf = toByteArray(getResource("test2.pdf"));
        byte[] expectedMergedPdf = toByteArray(getResource("merged.pdf"));

        given(duplexPreparator.prepare(test1Pdf)).willReturn(test1Pdf);
        given(duplexPreparator.prepare(test2Pdf)).willReturn(test2Pdf);

        // when
        byte[] pdfContent = pdfCreator.createFromBase64Pdfs(asList(test1Pdf, test2Pdf));

        // then
        try (
            InputStream actualPdfPage1 = getPdfPageContents(pdfContent, 0);
            InputStream actualPdfPage2 = getPdfPageContents(pdfContent, 1);

            InputStream expectedPdfPage1 = getPdfPageContents(expectedMergedPdf, 0);
            InputStream expectedPdfPage2 = getPdfPageContents(expectedMergedPdf, 1)
        ) {
            assertThat(actualPdfPage1).hasSameContentAs(expectedPdfPage1);
            assertThat(actualPdfPage2).hasSameContentAs(expectedPdfPage2);
        }

        // and
        verify(duplexPreparator, times(2)).prepare(any(byte[].class));
    }

    private InputStream getPdfPageContents(byte[] pdf, int pageNumber) throws Exception {
        try (PDDocument doc = PDDocument.load(pdf)) {
            byte[] data = ByteStreams.toByteArray(doc.getPage(pageNumber).getContents());
            return new ByteArrayInputStream(data);
        }
    }

}
