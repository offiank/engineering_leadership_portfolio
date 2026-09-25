package com.ankur.req2task.ingest;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TextParser implements DocumentParser {
    @Override
    public String parse(byte[] content) {
        return new String(content, StandardCharsets.UTF_8);
    }

    @Override
    public String[] supportedExtensions() {
        return new String[] { "txt", "md" };
    }
}
