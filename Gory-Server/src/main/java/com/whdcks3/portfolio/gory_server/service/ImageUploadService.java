package com.whdcks3.portfolio.gory_server.service;

import org.springframework.stereotype.Service;

@Service
public class ImageUploadService {

    private static final String UPLOAD_DIR = "uploads/";

    public List<String> uploadImages(List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                try {
                    String uniqueFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    Path path = Paths.get(UPLOAD_DIR + uniqueFileName);
                    image.transferTo(new File(path.toString()));
                    imageUrls.add("/uploads/" + uniqueFileName);
                } catch (IOException e) {
                    throw new RuntimeException("이미지 업로드 실패", e);
                }
            }
        }

        return imageUrls;
    }
}