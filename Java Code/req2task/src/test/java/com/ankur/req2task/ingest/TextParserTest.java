package com.ankur.req2task.ingest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextParserTest {

    private TextParser textParser;

    @BeforeEach
    void setUp() {
        textParser = new TextParser();
    }

    @Test
    void testParse() {
        String originalText = "Hello, this is a test text document.\nWith multiple lines.";
        String parsed = textParser.parse(originalText.getBytes());
        assertEquals(originalText, parsed);
    }

    @Test
    void testSupportedExtensions() {
        String[] extensions = textParser.supportedExtensions();
        assertArrayEquals(new String[]{"txt", "md"}, extensions);
    }
}
