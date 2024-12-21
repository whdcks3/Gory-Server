package com.whdcks3.portfolio.gory_server.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.feed.FeedComment;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    Optional<FeedComment> findByParentCommentPidAndPid(Long feedCommentId, Long pid);
}
