package com.ankur.req2task.ingest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentParserFactoryTest {

    @Mock
    private DocumentParser mockTextParser;

    @Mock
    private DocumentParser mockPdfParser;

    private DocumentParserFactory factory;

    @BeforeEach
    void setUp() {
        when(mockTextParser.supportedExtensions()).thenReturn(new String[]{"txt"});
        when(mockPdfParser.supportedExtensions()).thenReturn(new String[]{"pdf"});

        factory = new DocumentParserFactory(List.of(mockTextParser, mockPdfParser));
    }

    @Test
    void testIsSupported() {
        assertTrue(factory.isSupported("document.txt"));
        assertTrue(factory.isSupported("presentation.pdf"));
        assertFalse(factory.isSupported("image.png"));
        assertFalse(factory.isSupported("no_extension_file"));
    }

    @Test
    void testParseSupportedFormat() throws Exception {
        byte[] content = "test content".getBytes();
        when(mockTextParser.parse(content)).thenReturn("parsed text");

        String result = factory.parse("test.txt", content);
        assertEquals("parsed text", result);
        verify(mockTextParser, times(1)).parse(content);
    }

    @Test
    void testParseUnsupportedFormat() {
        byte[] content = "test content".getBytes();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            factory.parse("image.png", content);
        });

        assertEquals("Unsupported format: png", exception.getMessage());
    }
    
    @Test
    void testExtractExtensionEdgeCases() {
        assertFalse(factory.isSupported(".hiddenfile")); // dot is at 0, no extension
        assertFalse(factory.isSupported("file.")); // dot is at the end, empty extension
    }
}
