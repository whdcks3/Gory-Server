package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whdcks3.portfolio.gory_server.service.ImageUploadService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/squad/chat")
public class SquadChatRestController {
    private final ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> postMethodName(@RequestParam("images") List<MultipartFile> images) {
        return ResponseEntity.ok(imageUploadService.uploadImages(images));
    }

}
