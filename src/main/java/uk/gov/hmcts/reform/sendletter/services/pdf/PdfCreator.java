package uk.gov.hmcts.reform.sendletter.services.pdf;

import org.apache.http.util.Asserts;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.sendletter.model.in.Doc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class PdfCreator {

    private final DuplexPreparator duplexPreparator;

    public PdfCreator(DuplexPreparator duplexPreparator) {
        this.duplexPreparator = duplexPreparator;
    }

    public byte[] createFromBase64Pdfs(List<byte[]> base64decodedDocs) {
        Asserts.notNull(base64decodedDocs, "base64decodedDocs");

        List<byte[]> docs = base64decodedDocs
            .stream()
            .map(duplexPreparator::prepare)
            .collect(toList());

        return PdfMerger.mergeDocuments(docs);
    }

    public byte[] createFromBase64PdfWithCopies(List<Doc> docs) {
        Asserts.notNull(docs, "base64decodedDocs");

        List<byte[]> pdfs = docs
            .stream()
            .map(doc -> new Doc(duplexPreparator.prepare(doc.content), doc.copies))
            .map(d -> Collections.nCopies(d.copies, d.content))
            .flatMap(Collection::stream)
            .collect(toList());

        return PdfMerger.mergeDocuments(pdfs);
    }
}
