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

import com.google.rpc.context.AttributeContext.Response;
import com.whdcks3.portfolio.gory_server.data.dto.FeedSimpleDto;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;
import com.whdcks3.portfolio.gory_server.data.responses.CommonResponse;
import com.whdcks3.portfolio.gory_server.exception.ValidationException;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.service.FeedService;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    FeedService feedService;

    // 피드 생성
    @PostMapping("/create")
    public ResponseEntity<?> createFeed(Authentication authentication, @RequestBody FeedRequest req) {
        try {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getPid();
            feedService.createFeed(req, userId);
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // 피드 수정
    @PostMapping("/modify/{id}")
    public ResponseEntity<?> modifyFeed(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok().body(null);
    }

    // 피드 삭제
    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeed(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok().body(null);

    }

}
