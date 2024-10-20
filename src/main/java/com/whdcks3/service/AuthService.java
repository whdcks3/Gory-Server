package com.whdcks3.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whdcks3.common.EmailUtils;
import com.whdcks3.data.models.Role;
import com.whdcks3.data.models.user.User;
import com.whdcks3.data.requests.SignupRequest;
import com.whdcks3.enums.ERole;
import com.whdcks3.repositories.RoleRepository;
import com.whdcks3.repositories.UserRepository;
import com.whdcks3.security.service.CustomUserDetails;

import java.util.Properties;

@Service
public class AuthService {

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomUserDetails customUserDetails;

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
    public void sendMmail() {
        emailUtils.sendEmail("jinmj6446@gmail.com", "테스트 메일", "본문 내용");
        // Properties props = new Properties();
    }
}
