package com.whdcks3.portfolio.gory_server.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.common.EmailUtils;
import com.whdcks3.portfolio.gory_server.data.models.Role;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.enums.ERole;
import com.whdcks3.portfolio.gory_server.repositories.RoleRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;

import java.util.Properties;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    // @Autowired
    // CustomUserDetails customUserDetails;

    @Autowired
    RoleRepository roleRepository;

    @Value("${upload.image.location}")
    String imageFolder;
    @Value("${get.image.location}")
    String imageUrl;

    public boolean signUp(SignupRequest req) {
        try {
            String imageName = "avatar_placeholder.png";
            Role role = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
            User user = new User(req, passwordEncoder.encode(req.getSnsType() + req.getSnsId()), role,
                    imageUrl + imageName,
                    imageFolder + imageName);
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    // TODO : 회원 정보 가져오기 func

    public String getUserSnsType(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.get().getSnsType();
    }

    // TODO : 회원 이메일,코드 검증 func
    public boolean validateUser(String email, String code) {

        return true;
    }

    // TODO : 회원에게 이메일 보내기
    public void sendEmail(String email, String title, String body) {
        emailUtils.sendEmail(email, title, body);
        // Properties props = new Properties();

        // TODO : 입력받은 이메일에게 랜덤코드 발송하기, 코드인증 유효 시간 설정
        // 회원가입을 재시도한 회원의 인증코드 존재여부, 메일 인증 링크로 인한 계정 활성화

        String str = String.valueOf(new Random().nextInt() % 999999);
        System.out.println("code:" + str);
        str = "*".repeat(6 - str.length()) + str;

        // char[] keys = new char[] {}
    }

}
