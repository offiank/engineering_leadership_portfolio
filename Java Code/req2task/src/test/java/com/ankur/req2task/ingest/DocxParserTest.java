package com.ankur.req2task.ingest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocxParserTest {

    private DocxParser docxParser;

    @BeforeEach
    void setUp() {
        docxParser = new DocxParser();
    }

    @Test
    void testParseInvalidDocx() {
        byte[] invalidDocxContent = "Not a DOCX".getBytes();
        assertThrows(Exception.class, () -> docxParser.parse(invalidDocxContent), "Parsing invalid DOCX should throw an exception");
    }

    @Test
    void testSupportedExtensions() {
        String[] extensions = docxParser.supportedExtensions();
        assertArrayEquals(new String[]{"docx", "doc"}, extensions);
    }
}
