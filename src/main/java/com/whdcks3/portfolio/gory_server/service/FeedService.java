package com.whdcks3.portfolio.gory_server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.whdcks3.portfolio.gory_server.data.dto.FeedLikeDto;
import com.whdcks3.portfolio.gory_server.data.dto.FeedSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.Block;
import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedComment;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedImage;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedLike;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.FeedCommentRequest;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;
import com.whdcks3.portfolio.gory_server.data.responses.DataResponse;
import com.whdcks3.portfolio.gory_server.exception.MemberNotEqualsException;
import com.whdcks3.portfolio.gory_server.firebase.FirebasePublisherUtil;
import com.whdcks3.portfolio.gory_server.repositories.BlockRespository;
import com.whdcks3.portfolio.gory_server.repositories.FeedCommentRepository;
import com.whdcks3.portfolio.gory_server.repositories.FeedImageRepository;
import com.whdcks3.portfolio.gory_server.repositories.FeedLikeRepository;
import com.whdcks3.portfolio.gory_server.repositories.FeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final FeedImageRepository feedImageRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final FirebasePublisherUtil firebasePublisherUtil;
    private final BlockRespository blockRespository;

    // Feed CUD

    public FeedSimpleDto createFeed(FeedRequest req, User user) {
        List<FeedImage> images = req.getAddedImages().stream().map(image -> new FeedImage(image.getOriginalFilename()))
                .toList();
        Feed feed = feedRepository.save(new Feed(user, req, images));
        if (req.getAddedImages() != null) {
            saveFeedImages(feed, req.getAddedImages());
        }
        return FeedSimpleDto.toDto(feed, false);
    }

    @Transactional
    public FeedSimpleDto updateFeed(FeedRequest req, User user, Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(null);
        validateUser(user, feed);

        if (req.getDeletedImages() != null) {
            deleteImages(req.getDeletedImages());
        }
        if (req.getAddedImages() != null) {
            saveFeedImages(feed, req.getAddedImages());
        }
        feed.update(req);
        return FeedSimpleDto.toDto(feed, hasFeedLike(user, feed));
    }

    @Transactional
    public void deleteFeed(User user, Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new RuntimeException("Feed not found"));
        validateUser(user, feed);
        List<FeedImage> images = feedImageRepository.findByFeed(feed);
        images.forEach(image -> {
            fileService.delete(image.getImageUrl());
            feedImageRepository.delete(image);
        });
        feedRepository.delete(feed);
    }
    // Feed fetch

    public DataResponse othersFeed(Long userId, Long otherId, Pageable pageable) {
        User other = userRepository.findById(otherId).orElseThrow(null);
        User user = userRepository.findById(userId).orElseThrow(null);
        Page<Feed> feeds = feedRepository.findByUser(other, pageable);
        List<FeedSimpleDto> feedDtos = feeds.getContent().stream()
                .map(f -> FeedSimpleDto.toDto(f, hasFeedLike(user, f))).toList();
        return new DataResponse(feeds.hasNext(), feedDtos);
    }

    // public DataResponse feeds(User user, int page, String category) {
    public DataResponse homeFeed(User user, Pageable pageable, String category) {
        List<User> excludedUsers = new ArrayList<>();
        if (user != null) {
            excludedUsers = getExcludedUsers(user);
        }
        Page<Feed> feeds;
        if (category.equals("전체")) {
            feeds = feedRepository.findByUserNotIn(excludedUsers, pageable);
        } else {
            feeds = feedRepository.findByCategoryAndUserNotIn(category, excludedUsers, pageable);
        }
        boolean hasNext = feeds.hasNext();
        // boolean hasNext = ((long) Math.ceil((double) feeds.size() / 20)) > page + 1;
        List<FeedSimpleDto> feedDtos = feeds.getContent().stream()
                .map(f -> FeedSimpleDto.toDto(f, hasFeedLike(user, f))).toList();
        return new DataResponse(hasNext, feedDtos);
    }

    public DataResponse myFeeds(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(null);
        Page<Feed> feeds = feedRepository.findByUser(user, pageable);
        List<FeedSimpleDto> feedDtos = feeds.getContent().stream()
                .map(f -> FeedSimpleDto.toDto(f, hasFeedLike(user, f))).toList();
        return new DataResponse(feeds.hasNext(), feedDtos);
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
        feedLikeRepository.save(feedLike);

        String fcmToken = feed.getUser().getFcmToken();
        boolean isAlarm = feed.getUser().getFeedLikeAlarm();
        String title = "게시글 좋아요";
        String content = String.format("회원님의 게시글 [%s]를 좋아합니다.",
                feed.getContent().substring(0, Math.max(20, feed.getContent().length())));
        String data = feed.getPid() + ",feed";
        sendPushToClient(isAlarm, fcmToken, title, content, data);
    }

    public void removeFeedLike(Feed feed, User user) {
        FeedLike feedLike = feedLikeRepository.findByFeedAndUser(feed, user).orElseThrow(null);
        feedLikeRepository.delete(feedLike);
    }

    public boolean hasFeedLike(User user, Feed feed) {
        return feedLikeRepository.existsByFeedAndUser(feed, user);
    }

    // Feed Like END

    // Comments start
    public Long writeComment(Long uid, FeedCommentRequest req) {
        User user = userRepository.findById(uid).get();
        Feed feed = feedRepository.findById(req.getFeedPid()).get();
        FeedComment parentComment = feedCommentRepository.findById(req.getFeedCommentPid()).orElse(null);
        FeedComment comment = feedCommentRepository
                .save(new FeedComment(user, feed, req.getContent(), parentComment));
        feed.increaseCommentCount();
        feedRepository.save(feed);

        String fcmToken, pushTitle;
        boolean isAlarm;
        if (req.getFeedCommentPid() == null) {
            fcmToken = feed.getUser().getFcmToken();
            isAlarm = feed.getUser().getFeedAlarm();
            pushTitle = "작성하신 피드의 새로운 댓글이에요";
        } else {
            fcmToken = parentComment.getUser().getFcmToken();
            isAlarm = parentComment.getUser().getFeedAlarm();
            pushTitle = "작성하신 댓글의 새로운 답글이에요";
        }
        sendPushToClient(isAlarm, fcmToken, pushTitle, req.getContent(), feed.getPid() + ",feed");
        return comment.getPid();
    }

    // TODO : comments delete,차단기능
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        FeedComment comment = feedCommentRepository.findByParentCommentPidAndPid(commentId,
                userId).orElseThrow(null);
        feedCommentRepository.delete(comment);
    }

    @Transactional
    private void deleteCommentsByUser(User user) {
        List<FeedComment> comments = feedCommentRepository.findAllByUser(user);
        for (FeedComment feedComment : comments) {
            feedComment.getFeed().decreaseCommentCount();
            feedRepository.save(feedComment.getFeed());
        }
        feedCommentRepository.deleteAll(comments);
    }

    @Transactional
    private void deleteLikeByUser(User user) {
        List<FeedLike> likes = feedLikeRepository.findAllByUser(user);
        for (FeedLike feedLike : likes) {
            feedLike.getFeed().decreaseLikeCount();
            feedRepository.save(feedLike.getFeed());
        }
        feedLikeRepository.deleteAll(likes);
    }

    @Transactional
    private void deleteFeedByUser(User user) {
        List<Feed> feeds = feedRepository.findAllByUser(user);
        for (Feed feed : feeds) {
            feedCommentRepository.deleteAll(feed.getComments());
            feedLikeRepository.deleteAll(feedLikeRepository.findAllByFeed(feed));
            deleteFeed(user, feed.getPid());
        }
    }

    @Transactional
    public void deleteByUser(User user) {
        deleteCommentsByUser(user);
        deleteLikeByUser(user);
        deleteFeedByUser(user);
    }

    // Validations

    public void validateUser(User user, Feed feed) {
        if (!user.equals(feed.getUser())) {
            throw new MemberNotEqualsException();
        }
    }

    // Process Images

    private void saveFeedImages(Feed feed, List<MultipartFile> images) {
        List<FeedImage> feedImages = images.stream().map(image -> {
            String filename = fileService.upload(image);
            FeedImage feedImage = new FeedImage(filename, feed);
            return feedImage;
        }).collect(Collectors.toList());

        feedImageRepository.saveAll(feedImages);
    }

    private void deleteImages(List<String> imageUrls) {
        imageUrls.forEach(imageUrl -> {
            fileService.delete(imageUrl);
            feedImageRepository.deleteByImageUrl(imageUrl);
        });
    }

    private String sendPushToClient(boolean isAlarm, String token, String title, String content, String data) {
        if (isAlarm && token != null) {
            try {
                return firebasePublisherUtil.postToClient(title, content, data, token);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<User> getExcludedUsers(User user) {
        List<User> blockedUsers = blockRespository.findByBlocker(user).stream().map(Block::getBlocked)
                .collect(Collectors.toList());
        List<User> blockedByUsers = blockRespository.findByBlocked(user).stream().map(Block::getBlocker)
                .collect(Collectors.toList());
        blockedUsers.addAll(blockedByUsers);
        return blockedUsers.stream().distinct().toList();
    }
}
