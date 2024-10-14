package com.whdcks3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whdcks3.data.models.Role;
import com.whdcks3.data.models.user.User;
import com.whdcks3.data.requests.SignupRequest;
import com.whdcks3.enums.ERole;
import com.whdcks3.repositories.RoleRepository;
import com.whdcks3.repositories.UserRepository;
import com.whdcks3.security.service.CustomUserDetails;

@Service
public class AuthService {

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

        return "";
    }

    // TODO : 회원 이메일,코드 검증 func
    public boolean validateUser(String email, String code) {

        return true;
    }
}
