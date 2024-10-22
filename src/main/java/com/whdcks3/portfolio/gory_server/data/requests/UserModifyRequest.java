package com.whdcks3.portfolio.gory_server.data.requests;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModifyRequest {
    private String name, phone, introduction;
    private List<String> categorylist;
    private MultipartFile image;
}
