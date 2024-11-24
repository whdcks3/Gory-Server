package com.whdcks3.portfolio.gory_server.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whdcks3.portfolio.gory_server.data.dto.FeedSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedImage;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;
import com.whdcks3.portfolio.gory_server.exception.MemberNotEqualsException;
import com.whdcks3.portfolio.gory_server.repositories.FeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FeedService {
    private FeedRepository feedRepository;
    private UserRepository userRepository;
    private FileService fileService;

    @Value("${upload.image.location}")
    String imageFolder;

    @Value("${get.image.location}")
    String imageUrl;

    public FeedSimpleDto createFeed(FeedRequest req, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        List<FeedImage> images = req.getAddedImages().stream().map(image -> new FeedImage(image.getOriginalFilename()))
                .toList();
        Feed feed = feedRepository.save(new Feed(user, req, images));

        uploadImages(feed.getImages(), req.getAddedImages());
        return FeedSimpleDto.toDto(feed, imageUrl);
    }

    private void uploadImages(List<FeedImage> images, List<MultipartFile> fileImages) {
        IntStream.range(0, images.size())
                .forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    private void deletedImages(List<FeedImage> images) {
        images.forEach(i -> fileService.delete(i.getUniqueName()));
    }

    // 작성자와 일치 여부
    public void validateUser(User user, Feed feed) {
        if (!user.equals(feed.getUser())) {
            throw new MemberNotEqualsException();
        }
    }
}
