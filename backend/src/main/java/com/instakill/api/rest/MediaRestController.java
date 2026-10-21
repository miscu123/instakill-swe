package com.instakill.api.rest;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaRestController {

    @Value("${media.base-path:media}")
    private String mediaBasePath;

    // Allowed extensions (Optional task)
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".mp4", ".mov");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(mediaBasePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create media directory at startup", e);
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid file extension"));
            }

            String newFilename = UUID.randomUUID() + extension;
            Path filePath = Paths.get(mediaBasePath, newFilename);

            file.transferTo(filePath.toFile());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mediaUrl", "/media/" + newFilename));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file"));
        }
    }
}