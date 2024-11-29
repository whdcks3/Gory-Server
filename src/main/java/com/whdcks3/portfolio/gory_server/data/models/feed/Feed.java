package com.whdcks3.portfolio.gory_server.data.models.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.common.CommonVO;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feed")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
public class Feed extends CommonVO {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 5000)
    private String content;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<FeedImage> images;

    @JsonManagedReference
    @OneToMany(mappedBy = "feed", targetEntity = FeedComment.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FeedComment> comments = new ArrayList<>();

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int likeCount, viewCount, commetCount, reportCount;

    @lombok.Builder
    public Feed(User user, FeedRequest req, List<FeedImage> images) {
        this.user = user;
        this.category = req.getCategory();
        this.content = req.getContent();
        this.likeCount = 0;
        this.viewCount = 0;
        this.reportCount = 0;
        this.images = new ArrayList<>();
        addImages(images);
    }

    public ImageUpdatedResult update(FeedRequest req) {
        this.content = req.getContent();
        this.category = req.getCategory();
        ImageUpdatedResult result = findImageUpdatedResult(req.getAddedImages(), req.getDeletedImages());
        addImages(result.getAddedImages());
        deleteImages(result.getDeletedImages());
        return result;
    }

    private void addImages(List<FeedImage> added) {
        added.stream().forEach(i -> {
            images.add(i);
            i.initFeed(this);
        });
        System.out.println(added.size());
    }

    private void deleteImages(List<FeedImage> deleted) {
        deleted.stream().forEach(di -> this.images.remove(di));
    }

    private ImageUpdatedResult findImageUpdatedResult(List<MultipartFile> addedImageFiles,
            List<Integer> deletedImageIds) {
        List<FeedImage> addedImages = convertImageFillesToImages(addedImageFiles);
        List<FeedImage> deletedImages = convertImageIdsToImages(deletedImageIds);
        return new ImageUpdatedResult(addedImageFiles, addedImages, deletedImages);
    }

    private List<FeedImage> convertImageIdsToImages(List<Integer> imageIds) {
        return imageIds.stream()
                .map(id -> convertImageIdToImage(id))
                .filter(i -> i.isPresent())
                .map(i -> i.get())
                .toList();
    }

    private Optional<FeedImage> convertImageIdToImage(int id) {
        return this.images.stream().filter(i -> i.getId() == (id)).findAny();
    }

    private List<FeedImage> convertImageFillesToImages(List<MultipartFile> imageFiles) {
        return imageFiles.stream().map(imageFile -> new FeedImage(imageFile.getOriginalFilename())).toList();
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void decreaseLikeCount() {
        this.likeCount -= 1;
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void decreaseCommentCount() {
        this.commetCount -= 1;
    }

    public void increaseCommentCount() {
        this.commetCount += 1;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    @Getter
    @AllArgsConstructor
    public static class ImageUpdatedResult {
        private List<MultipartFile> addedImageFiles;
        private List<FeedImage> addedImages;
        private List<FeedImage> deletedImages;
    }
}
