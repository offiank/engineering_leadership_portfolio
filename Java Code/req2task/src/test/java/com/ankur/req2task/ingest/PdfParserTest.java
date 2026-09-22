package com.ankur.req2task.ingest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfParserTest {

    private PdfParser pdfParser;

    @BeforeEach
    void setUp() {
        pdfParser = new PdfParser();
    }

    @Test
    void testParseInvalidPdf() {
        byte[] invalidPdfContent = "Not a PDF".getBytes();
        assertThrows(Exception.class, () -> pdfParser.parse(invalidPdfContent), "Parsing invalid PDF should throw an exception");
    }

    @Test
    void testSupportedExtensions() {
        String[] extensions = pdfParser.supportedExtensions();
        assertArrayEquals(new String[]{"pdf"}, extensions);
    }
}
