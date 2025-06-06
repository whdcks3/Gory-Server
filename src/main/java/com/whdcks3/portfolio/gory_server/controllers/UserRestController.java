package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.service.UserService;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/generate_username")
    public ResponseEntity<?> generateUserName() {
        return ResponseEntity.ok(userService.generateNickname());
    }

    @PutMapping("/update_nickname")
    public ResponseEntity<?> checkNickname(@AuthenticationPrincipal User user, @RequestParam String nickname) {
        userService.updateNickname(user, nickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/limit_username")
    public ResponseEntity<?> limitName(@AuthenticationPrincipal User user, @RequestParam String nickname) {
        userService.limitNickname(nickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/find_account")
    public ResponseEntity<?> verificationEmail(@RequestParam String email) {
        try {
            userService.sendEmailLink(email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error occurred");
        }
        return ResponseEntity.ok("Email verification successful");
    }

    @PutMapping("/modify_password")
    public ResponseEntity<?> modifyPassword(@RequestParam String email, @RequestParam String snsType,
            @RequestParam String snsId, @RequestParam String password) {
        userService.modifyPassword(email, snsType, snsId, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update_fcm")
    public ResponseEntity<?> updateFcm(@AuthenticationPrincipal User user, @RequestParam String token) {
        userService.updateFcm(user, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/has_username")
    public ResponseEntity<?> hasUsername(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.hasUsername(user));
    }

}
