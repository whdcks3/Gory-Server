package com.whdcks3.portfolio.gory_server.contorollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.exception.UsernameNotFoundException;
import com.whdcks3.portfolio.gory_server.exception.ValidationException;
import com.google.rpc.context.AttributeContext.Response;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.responses.CommonResponse;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/generate_username")
    public ResponseEntity<?> generateUserName() {
        try {
            userService.generateNickname();
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    @PostMapping("/update_nickname")
    public ResponseEntity<?> checkNickname(Authentication authentication, @RequestParam String nickname) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userService.updateNickname(userDetails.getPid(), nickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/limit_username")
    public ResponseEntity<?> limitName(Authentication authentication, @RequestParam String user) {
        try {
            return ResponseEntity.ok().body(new CommonResponse(100, userService.limitNickname(user)));
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
    }

    @PostMapping("/find_account")
    public ResponseEntity<?> verificationEmail(@RequestParam String email) {
        try {
            userService.sendEmailLink(email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("오류 발생");
        }
        return ResponseEntity.ok("이메일 인증 완료");
    }

    @PostMapping("/modify_password")
    public ResponseEntity<?> modifyPassword(@RequestParam String email, @RequestParam String snsType,
            @RequestParam String snsId, @RequestParam String password) {
        try {
            userService.modifyPassword(email, snsType, snsId, password);
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }
}
