package com.ankur.req2task.ingest;

import org.springframework.stereotype.Component;

@Component
public class TextParser implements DocumentParser {
    @Override
    public String parse(byte[] content) {
        return new String(content);
    }

    @Override
    public String[] supportedExtensions() {
        return new String[] { "txt", "md" };
    }
}
