package com.whdcks3.portfolio.gory_server.contorollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.service.UserService;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity<?> generateUserName() {
        String nickname = userService.generateNickname();
        User user = new User(nickname);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean check = userService.duplicationNickname(nickname);
        if (check) {
            return ResponseEntity.badRequest().body("누군가 사용중인 이름입니다. 다른 이름을 사용해주세요");
        } else {
            return ResponseEntity.ok().body("사용 가능한 이름입니다.");
        }
    }
}
