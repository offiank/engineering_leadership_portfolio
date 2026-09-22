package com.ankur.req2task.ingest;

public interface DocumentParser {
    String parse(byte[] content) throws Exception;

    String[] supportedExtensions();
}