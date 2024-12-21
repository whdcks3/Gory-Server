package com.whdcks3.portfolio.gory_server.data.models.feed;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.springframework.format.annotation.DateTimeFormat;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.exception.UnSupportedImageFormatException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Getter
public class FeedImage extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_pid", nullable = false)
    private Feed feed;

    private final static String supportedExtension[] = { "jpg", "jpeg", "gif", "bmp", "png" };

    public FeedImage(String originName) {
        this.imageUrl = generateUniqueName(extractExtension(originName));
    }

    public void initFeed(Feed feed) {
        if (this.feed == null) {
            this.feed = feed;
        }
    }

    private String generateUniqueName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String extractExtension(String originName) {
        try {
            System.out.println("originName: " + originName);
            String ext = originName.substring(originName.lastIndexOf(".") + 1);
            System.out.println("originName: " + originName + " , ext: " + ext);
            if (isSupportedFormat(ext))
                return ext;
        } catch (StringIndexOutOfBoundsException e) {
        }
        throw new UnSupportedImageFormatException();
    }

    private boolean isSupportedFormat(String ext) {
        return Arrays.stream(supportedExtension).anyMatch(e -> e.equalsIgnoreCase(ext));
    }
}
