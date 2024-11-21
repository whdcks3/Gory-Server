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
import com.whdcks3.portfolio.gory_server.repositories.FeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

@Service
public class FeedService {

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileService fileService;

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
    } // 컨트롤러 테스트, 수정, 작성자와 일치 여부

    private void uploadImages(List<FeedImage> images, List<MultipartFile> fileImages) {
        IntStream.range(0, images.size())
                .forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    private void deletedImages(List<FeedImage> images) {
        images.forEach(i -> fileService.delete(i.getUniqueName()));
    }
}
