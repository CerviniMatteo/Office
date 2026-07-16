package com.unimib.GUI.UI.view.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FileUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private FileUtils() {
        // Utility class
    }

    public static <T> List<T> readObjects(Path file, Class<T> clazz) {
        if (!Files.exists(file)) {
            return List.of();
        }

        try (var lines = Files.lines(file)) {
            return lines
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        try {
                            return MAPPER.readValue(line, clazz);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + file, e);
        }
    }

    public static <T> void appendObject(Path file, T object) {
        try {
            Files.createDirectories(file.getParent());

            String json = MAPPER.writeValueAsString(object);

            Files.writeString(
                    file,
                    json + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            throw new RuntimeException("Error writing file: " + file, e);
        }
    }
}