package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class SquadChatImageRestController {
    private final ImageUploadService imageUploadService;

    public SquadChatImageController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("images") List<MultipartFile> images) {
        List<String> imageUrls = imageUploadService.uploadImages(images);
        return ResponseEntity.ok(imageUrls);
    }
}
