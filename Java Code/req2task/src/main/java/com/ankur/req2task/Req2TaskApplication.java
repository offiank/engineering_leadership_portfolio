package com.ankur.req2task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class Req2TaskApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(Req2TaskApplication.class, args);
    }

    /**
     * Reads key=value pairs from .env in project root and sets them as system properties
     * if not already present in the environment or system properties.
     */
    private static void loadDotEnv() {
        Path envPath = Paths.get(".env");
        if (!Files.exists(envPath)) {
            return;
        }

        try (var lines = Files.lines(envPath)) {
            lines.map(String::trim)
                 .filter(line -> !line.isEmpty() && !line.startsWith("#") && line.contains("="))
                 .forEach(line -> {
                     int separatorIdx = line.indexOf('=');
                     String key = line.substring(0, separatorIdx).trim();
                     String value = line.substring(separatorIdx + 1).trim();

                     // Strip matching quotes if present
                     if (value.length() >= 2 && 
                         ((value.startsWith("\"") && value.endsWith("\"")) || 
                          (value.startsWith("'") && value.endsWith("'")))) {
                         value = value.substring(1, value.length() - 1);
                     }

                     if (System.getProperty(key) == null && System.getenv(key) == null) {
                         System.setProperty(key, value);
                     }
                 });
        } catch (Exception ignored) {
            // Proceed with defaults if .env can't be read
        }
    }
}
