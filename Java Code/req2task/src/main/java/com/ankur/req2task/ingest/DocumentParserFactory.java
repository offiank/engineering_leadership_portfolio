package com.ankur.req2task.ingest;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DocumentParserFactory {
    private final Map<String, DocumentParser> parsersByExtension;

    public DocumentParserFactory(List<DocumentParser> parsers) {
        Map<String, DocumentParser> map = new HashMap<>();
        for (DocumentParser parser : parsers) {
            for (String ext : parser.supportedExtensions()) {
                map.put(ext, parser);
            }
        }
        this.parsersByExtension = map;
    }

    public boolean isSupported(String filename) {
        return parsersByExtension.containsKey(extractExtension(filename));
    }

    public String parse(String filename, byte[] content) throws Exception {
        String ext = extractExtension(filename);
        DocumentParser parser = parsersByExtension.get(ext);
        if (parser == null) {
            throw new IllegalArgumentException("Unsupported format: " + ext);
        }
        return parser.parse(content);
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1)
            return "";
        return filename.substring(dot + 1).toLowerCase();
    }
}
