package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeedSimpleDto {

    private String nickname;
    private String category;
    private String content;
    private List<String> images;
    private boolean like;
    private String datetime; // 2024-11-21T14:14:00

    public static FeedSimpleDto toDto(Feed feed, String url, boolean isLike) {
        String nickname = feed.getUser().getNickname();
        String category = feed.getCategory();
        String content = feed.getContent();
        List<String> images = feed.getImages().stream().map(image -> url + image.getUniqueName()).toList();
        String datetime = feed.getRegDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new FeedSimpleDto(nickname, category, content, images, isLike, datetime);
    }
}
