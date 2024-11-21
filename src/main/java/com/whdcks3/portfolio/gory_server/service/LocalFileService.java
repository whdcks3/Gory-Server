package com.whdcks3.portfolio.gory_server.service;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.Value;
import com.whdcks3.portfolio.gory_server.exception.FileUploadFailureException;

@Service
public class LocalFileService implements FileService {

    @Value("${upload.image.location}")
    String location;

    @PostConstruct
    void postConstruct() {
        File dir = new File(location);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    public void upload(MultipartFile file, String fileName) {
        try {
            file.transferTo(new File(location + fileName));
        } catch (IOException e) {
            throw new FileUploadFailureException(e);
        }

    }

    @Override
    public void delete(String fileName) {
        new File(location + fileName).delete();

    }
}
