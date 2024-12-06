package com.whdcks3.portfolio.gory_server.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.whdcks3.portfolio.gory_server.data.dto.FeedLikeDto;
import com.whdcks3.portfolio.gory_server.data.dto.FeedSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedImage;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedLike;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;
import com.whdcks3.portfolio.gory_server.data.responses.DataResponse;
import com.whdcks3.portfolio.gory_server.exception.MemberNotEqualsException;
import com.whdcks3.portfolio.gory_server.repositories.FeedLikeRespository;
import com.whdcks3.portfolio.gory_server.repositories.FeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final FeedLikeRespository feedLikeRespository;

    @Value("${upload.image.location}")
    String imageFolder;

    @Value("${get.image.location}")
    String imageUrl;

    // Feed CUD

    public FeedSimpleDto createFeed(FeedRequest req, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        List<FeedImage> images = req.getAddedImages().stream().map(image -> new FeedImage(image.getOriginalFilename()))
                .toList();
        Feed feed = feedRepository.save(new Feed(user, req, images));

        uploadImages(feed.getImages(), req.getAddedImages());
        return FeedSimpleDto.toDto(feed, imageUrl, false);
    }

    public FeedSimpleDto updateFeed(FeedRequest req, Long userId, Long feedId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        Feed feed = feedRepository.findById(feedId).orElseThrow(null);
        validateUser(user, feed);
        Feed.ImageUpdatedResult result = feed.update(req);
        deletedImages(result.getDeletedImages());
        return FeedSimpleDto.toDto(feed, imageUrl, hasFeedLike(user, feed));
    }

    @Transactional
    public void deleteFeed(Long userId, Long feedId) {
        User user = userRepository.findById(userId).orElseThrow(null);
        Feed feed = feedRepository.findById(feedId).orElseThrow(null);
        validateUser(user, feed);
        feedRepository.delete(feed);
    }
    // Feed fetch
    // TO DO : 상대방 피드 가져오기(otherFeeds/id), 전체 피드 가져오기(home)

    public DataResponse othersFeed(Long userId, Long otherId, int page) {
        User other = userRepository.findById(otherId).orElseThrow(null);
        User user = userRepository.findById(userId).orElseThrow(null);
        List<Feed> feeds = feedRepository.findAllByUserOrderByRegDtDesc(other);
        boolean hasNext = ((long) Math.ceil((double) feeds.size() / 20)) > page + 1;
        List<FeedSimpleDto> feedDtos = feeds.stream().skip(page * 20).limit(20)
                .map(f -> FeedSimpleDto.toDto(f, imageUrl, hasFeedLike(user, f))).toList();
        return new DataResponse(hasNext, feedDtos);
    }

    // public DataResponse feeds(User user, int page, String category) {
    public DataResponse feeds(User user, Pageable pageable, String category) {
        Page<Feed> feeds;
        // if (category.equals("전체")) {
        // // category = "";
        // feeds = feedRepository.findAllByOrderByRegDtDesc();
        // } else {
        // feeds = feedRepository.findAllByCategoryOrderByRegDtDesc(category);
        // }
        if (category.equals("전체")) {
            // category = "";
            feeds = feedRepository.findAll(pageable);
        } else {
            feeds = feedRepository.findAllByCategory(category, pageable);
        }
        boolean hasNext = feeds.hasNext();
        // boolean hasNext = ((long) Math.ceil((double) feeds.size() / 20)) > page + 1;
        List<FeedSimpleDto> feedDtos = feeds.getContent().stream()
                .map(f -> FeedSimpleDto.toDto(f, imageUrl, hasFeedLike(user, f))).toList();
        return new DataResponse(hasNext, feedDtos);
    }

    public DataResponse myFeeds(Long userId, int page) {
        User user = userRepository.findById(userId).orElseThrow(null);
        List<Feed> feeds = feedRepository.findAllByUserOrderByRegDtDesc(user);
        boolean hasNext = ((long) Math.ceil((double) feeds.size() / 20)) > page + 1;
        List<FeedSimpleDto> feedDtos = feeds.stream().skip(page * 20).limit(20)
                .map(f -> FeedSimpleDto.toDto(f, imageUrl, hasFeedLike(user, f))).toList();
        return new DataResponse(hasNext, feedDtos);
    }

    // Feed Like START

    @Transactional
    public FeedLikeDto processFeedLike(Long uId, Long fId) {
        User user = userRepository.findById(uId).orElseThrow(null);
        Feed feed = feedRepository.findById(fId).orElseThrow(null);
        boolean isNew = !hasFeedLike(user, feed);
        if (isNew) {
            feed.increaseLikeCount();
            createFeedLike(feed, user);
        } else {
            feed.decreaseLikeCount();
            removeFeedLike(feed, user);
        }
        feedRepository.save(feed);
        return FeedLikeDto.toDto(isNew, feed.getLikeCount());
    }

    public void createFeedLike(Feed feed, User user) {
        FeedLike feedLike = new FeedLike(feed, user);
        feedLikeRespository.save(feedLike);

    }

    public void removeFeedLike(Feed feed, User user) {
        FeedLike feedLike = feedLikeRespository.findByFeedAndUser(feed, user).orElseThrow(null);
        feedLikeRespository.delete(feedLike);
    }

    public boolean hasFeedLike(User user, Feed feed) {
        return feedLikeRespository.existsByFeedAndUser(feed, user);
    }

    // Feed Like END

    // Validations

    public void validateUser(User user, Feed feed) {
        if (!user.equals(feed.getUser())) {
            throw new MemberNotEqualsException();
        }
    }

    // Process Images

    private void uploadImages(List<FeedImage> images, List<MultipartFile> fileImages) {
        IntStream.range(0, images.size())
                .forEach(i -> fileService.upload(fileImages.get(i), images.get(i).getUniqueName()));
    }

    private void deletedImages(List<FeedImage> images) {
        images.forEach(i -> fileService.delete(i.getUniqueName()));
    }

}
