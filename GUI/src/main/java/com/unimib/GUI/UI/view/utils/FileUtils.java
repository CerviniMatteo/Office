package com.unimib.GUI.UI.view.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.net.URI;
import java.net.URL;

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

    public static byte[] readBytes(File file) {
        if(file == null){
            throw new RuntimeException("File is empty");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file", e);
        }
    }

    public static void setUpFileChooser(FileChooser fileChooser){
        File downloadFolder = new File(
                System.getProperty("user.home"),
                "Downloads"
        );

        if (downloadFolder.exists()) {
            fileChooser.setInitialDirectory(downloadFolder);
        }

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );
    }
}