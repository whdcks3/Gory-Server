package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.common.Utils;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.FeedCommentRequest;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;
import com.whdcks3.portfolio.gory_server.service.BlockService;
import com.whdcks3.portfolio.gory_server.service.FeedService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedRestController {

    @Autowired
    FeedService feedService;

    @Autowired
    BlockService blockService;

    // 피드 생성
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createFeed(@AuthenticationPrincipal User user, @ModelAttribute FeedRequest req) {
        feedService.createFeed(req, user);
        return ResponseEntity.ok().build();
        // try {
        // feedService.createFeed(req, user);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // 피드 수정
    @PutMapping("/modify/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> modifyFeed(@AuthenticationPrincipal User user, @PathVariable Long id,
            @ModelAttribute FeedRequest req) {
        feedService.updateFeed(req, user, id);
        return ResponseEntity.ok().build();
        // try {
        // feedService.updateFeed(req, user, id);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // 피드 삭제
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteFeed(@AuthenticationPrincipal User user, @PathVariable("id") Long fid) {
        feedService.deleteFeed(user, fid);
        return ResponseEntity.ok().build();
        // try {
        // feedService.deleteFeed(user, fid);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    @GetMapping("/like/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> likeFeed(@AuthenticationPrincipal User user, @PathVariable Long id) {
        feedService.processFeedLike(user.getPid(), id);
        return ResponseEntity.ok().build();
        // try {
        // feedService.processFeedLike(Utils.getPid(), id);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    @Operation(summary = "나의 피드", security = @SecurityRequirement(name = "bearerAuth"))
    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> mine(@AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        feedService.myFeeds(user.getPid(), pageable);
        return ResponseEntity.ok().build();
        // try {
        // feedService.myFeeds(Utils.getPid(), pageable);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // @RequestMapping(value = "/home", method = RequestMethod.GET)
    // public ResponseEntity<?> home(@RequestParam(value = "page", defaultValue =
    // "0") int page,
    // @RequestParam(value = "category", defaultValue = "전체") String category,
    // @AuthenticationPrincipal User user) {
    // System.out.println("user: " + (user != null));
    // return ResponseEntity.ok().body(feedService.feeds(user, page, category));
    // }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> home(@AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable,
            @RequestParam(defaultValue = "전체") String category) {
        System.out.println("user: " + (user != null));
        return ResponseEntity.ok().body(feedService.homeFeed(user, pageable, category));
    }

    @PostMapping("/others/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> other(@AuthenticationPrincipal User user, @PathVariable Long id,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        feedService.othersFeed(user.getPid(), id, pageable);
        return ResponseEntity.ok().build();
        // try {
        // feedService.othersFeed(Utils.getPid(), id, pageable);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    @PostMapping("/createcomment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal User user, @ModelAttribute FeedCommentRequest req) {
        feedService.writeComment(user.getPid(), req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletecomment/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        feedService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
        // try {
        // feedService.deleteComment(id, userId);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

}
