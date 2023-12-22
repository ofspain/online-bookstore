package com.interswitch.bookstore.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LogInitializer {

    @PostConstruct
    public void initializeLogDirectory() {
        String logDirectoryPath = "logs";

        // Create the log directory if it doesn't exist
        Path logDirectory = Paths.get(logDirectoryPath);
        if (Files.notExists(logDirectory)) {
            try {
                Files.createDirectories(logDirectory);
                System.out.println("Log directory created: " + logDirectory.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error creating log directory: " + e.getMessage());
            }
        } else {
            System.out.println("Log directory already exists: " + logDirectory.toAbsolutePath());
        }
    }
}
