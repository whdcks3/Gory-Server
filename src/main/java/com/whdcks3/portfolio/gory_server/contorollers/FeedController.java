package com.whdcks3.portfolio.gory_server.contorollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.database.utilities.Validation;
import com.google.rpc.context.AttributeContext.Response;
import com.whdcks3.portfolio.gory_server.common.Utils;
import com.whdcks3.portfolio.gory_server.data.dto.FeedSimpleDto;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;
import com.whdcks3.portfolio.gory_server.data.responses.CommonResponse;
import com.whdcks3.portfolio.gory_server.exception.ValidationException;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.service.FeedService;

import ch.qos.logback.classic.pattern.Util;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    FeedService feedService;

    // 피드 생성
    @PostMapping("/create")
    public ResponseEntity<?> createFeed(Authentication authentication, @RequestBody FeedRequest req) {
        try {
            feedService.createFeed(req, Utils.getPid());
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // 피드 수정
    @PostMapping("/modify/{id}")
    public ResponseEntity<?> modifyFeed(Authentication authentication, @PathVariable Long id,
            @RequestBody FeedRequest req) {
        try {
            feedService.updateFeed(req, Utils.getPid(), id);
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // 피드 삭제
    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeed(Authentication authentication, @PathVariable Long id) {
        try {
            feedService.deleteFeed(Utils.getPid(), id);
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));

    }

    @GetMapping("/like/{id}")
    public ResponseEntity<?> likeFeed(Authentication authentication, @PathVariable Long id) {
        try {
            feedService.processFeedLike(Utils.getPid(), id);
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    public ResponseEntity<?> mine(Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok().build();
    }

}
