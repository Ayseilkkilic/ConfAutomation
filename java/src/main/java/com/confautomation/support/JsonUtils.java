package com.confautomation.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T readJsonFileRelative(String fileName, TypeReference<T> type) throws IOException {
        // Try CWD, then parent of CWD
        Path p1 = Path.of(fileName);
        Path p2 = Path.of("..", fileName);
        if (Files.exists(p1)) {
            return mapper.readValue(p1.toFile(), type);
        } else if (Files.exists(p2)) {
            return mapper.readValue(p2.toFile(), type);
        }
        // Try project root up two levels (if running from java module)
        Path p3 = Path.of("..", "..", fileName);
        if (Files.exists(p3)) {
            return mapper.readValue(p3.toFile(), type);
        }
        throw new IOException("File not found: " + fileName + " (searched in . , .. , ../..)");
    }

    public static void writeJsonFileRelative(String fileName, Map<String, ?> data) throws IOException {
        Path p1 = Path.of(fileName);
        Path p2 = Path.of("..", fileName);
        Path target = Files.exists(p1) ? p1 : p2;
        mapper.writerWithDefaultPrettyPrinter().writeValue(target.toFile(), data);
    }
}

