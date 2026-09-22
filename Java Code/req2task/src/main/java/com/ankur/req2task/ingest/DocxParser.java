package com.ankur.req2task.ingest;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

@Component
public class DocxParser implements DocumentParser {
    @Override
    public String parse(byte[] content) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(content))) {
            return doc.getParagraphs().stream()
                    .filter(para -> para != null)
                    .map(para -> para.getText())
                    .filter(text -> text != null && !text.isBlank())
                    .collect(Collectors.joining("\n\n"));
        }
    }

    @Override
    public String[] supportedExtensions() {
        return new String[] { "docx", "doc" };
    }
}
