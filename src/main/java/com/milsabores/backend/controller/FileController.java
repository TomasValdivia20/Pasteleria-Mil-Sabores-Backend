package com.milsabores.backend.controller;

import com.milsabores.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService storageService;

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = storageService.store(file);

        // Construimos la URL relativa que guardaremos en la BBDD
        // Ej: "uploads/mi_imagen.jpg"
        Map<String, String> response = new HashMap<>();
        response.put("url", "uploads/" + filename);
        return response;
    }
}