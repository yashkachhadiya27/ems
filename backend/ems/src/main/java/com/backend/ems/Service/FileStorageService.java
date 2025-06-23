package com.backend.ems.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

@Service
public class FileStorageService {
    private final String fileStorageLocation = "./chat-files";

    public String storeFile(MultipartFile file) throws IOException {
        validateFileType(file); // Validate file type

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetLocation = Paths.get(fileStorageLocation).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(fileStorageLocation).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            return resource.exists() ? resource : null;
        } catch (Exception e) {
            throw new RuntimeException("File not found " + fileName, e);
        }
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/") && !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("Invalid file type: " + contentType);
        }
    }
}
