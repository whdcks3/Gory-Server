package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedImage;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

@Repository
public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {
    void deleteByImageUrl(String imageUrl);

    List<FeedImage> findByFeed(Feed feed);
}
