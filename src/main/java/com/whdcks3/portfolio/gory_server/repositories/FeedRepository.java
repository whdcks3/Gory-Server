package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    List<Feed> findAllByUserOrderByRegDtDesc(User user);
}
