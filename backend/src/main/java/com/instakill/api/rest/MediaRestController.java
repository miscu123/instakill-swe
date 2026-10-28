package com.instakill.api.rest;

import com.instakill.media.application.ImageProcessingService;
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

    private final ImageProcessingService imageProcessingService;

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(".mp4", ".mov");

    public MediaRestController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(mediaBasePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create media directory", e);
        }
    }

    @PostMapping
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                    : "";

            boolean isImage = IMAGE_EXTENSIONS.contains(extension);
            boolean isVideo = VIDEO_EXTENSIONS.contains(extension);

            if (!isImage && !isVideo) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid file extension"));
            }

            String newFilename = UUID.randomUUID() + extension;
            Path filePath = Paths.get(mediaBasePath, newFilename);

            if (isImage) {
                // Route through our new image processing service module
                imageProcessingService.processAndSaveImage(file.getInputStream(), filePath.toFile(), extension);
            } else {
                // Direct file transfer for video formats
                file.transferTo(filePath.toFile());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mediaUrl", "/media/" + newFilename));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process and upload file"));
        }
    }
}