package com.whdcks3.portfolio.gory_server.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.web.util.UriComponentsBuilder;

import com.whdcks3.portfolio.gory_server.common.EmailUtils;
import com.whdcks3.portfolio.gory_server.data.models.RandomCode;
import com.whdcks3.portfolio.gory_server.data.models.Role;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.enums.ERole;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.exception.ValidationException;
import com.whdcks3.portfolio.gory_server.repositories.RandomCodeRepository;
import com.whdcks3.portfolio.gory_server.repositories.RoleRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;

import java.util.Properties;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    RandomCodeRepository randomCodeRepository;

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
            sendActivationEmail(user);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getLocalizedMessage());
            return false;
        }

        return true;
    }
    // TODO : 회원 정보 가져오기 func

    public String getUserSnsType(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.get().getSnsType();
    }

    // 코드 이메일 검증 및, 인증 유효시간 설정
    public boolean validateUser(String email, String code) {
        RandomCode randomCode = randomCodeRepository.findByEmail(email);
        System.out.println(randomCode == null);
        if (randomCode == null) {
            throw new ValidationException(200, "이메일이 존재하지 않습니다.");
        }
        if (randomCode.isExpired()) {
            throw new ValidationException(201, "만료된 코드입니다.");
        }
        if (!randomCode.getCode().equals(code)) {
            throw new ValidationException(202, "일치하지 않은 코드입니다.");
        }

        return true;
    }

    public void sendActivationEmail(User user) {
        String activationLink = "http://localhost:3434/activate?token=" + user.getActivationToken();
        emailUtils.sendEmail(user.getEmail(), "계정 활성화", "다음 링크를 통해 계정을 활성화 해주세요. " + activationLink);
    }

    // 5번이상 인증 시 30분 잠금
    public boolean shouldLock(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user.isWithinLockedTime()) {
            System.out.println("계정 잠금, 해제 시간 : " + user.getLockedUntil());
            return false;
        }

        LocalDateTime time = LocalDateTime.now();

        // 1시간 이내 시도 횟수 삭제
        user.getAuthAttempts().removeIf(attempts -> attempts.isBefore(time.minusHours(1)));
        // 시도 횟수 추가
        user.getAuthAttempts().add(time);

        if (user.getAuthAttempts().size() > 5) {
            user.tooManyAttempts(time.plusMinutes(30));
            System.out.println("계정이 30분 잠겼습니다.");
        } else {
            user.setLockType(LockType.NONE);
            user.setLockedUntil(null);
        }
        userRepository.save(user);
        return true;
    }

    // 메일 인증 링크로 인한 계정 활성화
    public boolean activateUser(String token) {
        return userRepository.findByActivationToken(token)
                .filter(user -> user.getTokenExpiryDate().isAfter(LocalDateTime.now()))
                .map(user -> {
                    user.setLockType(LockType.NONE);
                    user.setActivationToken(null);
                    user.setTokenExpiryDate(null);
                    user.setLockedUntil(null);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
        // String activationLink = "http://localhost:3434/validate?token=" +
        // user.getActivationToken();

        // // builder.queryParam("email", email).queryParam("code",
        // code).build().toUriString();

        // RandomCode storedCode = randomCodeRepository.findByEmail(email);
        // if (storedCode == null) {
        // return "존재하지 않는 코드입니다.";
        // }
        // if (storedCode.isExpired()) {
        // return " 만료된 코드입니다.";
        // }
        // if (!storedCode.getCode().equals(storedCode)) {
        // return "코드가 일치하지 않습니다";
        // }

        // storedCode.isEnabled(true);
        // randomCodeRepository.save(storedCode);

        // return "코드 인증이 완료되었습니다.";
    }

    // 회원에게 이메일 보내기
    public void sendEmail(String email) {

        RandomCode existingCode = randomCodeRepository.findByEmail(email);
        if (existingCode != null && !existingCode.isExpired()) {
            System.out.println("이미 코드가 존재합니다" + existingCode.getCode());
        }

        String code = String.valueOf(Math.abs(new Random().nextInt()) % 999999);
        code = "*".repeat(6 - code.length()) + code;
        System.out.println("code:" + code);

        String title = "인증 코드";
        String body = "인증 코드는: " + code;

        emailUtils.sendEmail(email, title, body);

        System.out.println("생성 코드: " + code);

        RandomCode randomCode = new RandomCode(email, code);

        randomCodeRepository.save(randomCode);
    }
    // char[] keys = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    // };
    // String createdKey = "";
    // int pl = 0;
    // for (int i = 0; i < 6; i++) {
    // pl = (int) (Math.random() * keys.length);
    // createdKey += keys[pl];
    // }
    // System.out.println("code:" + createdKey);

    // 회원이 활성화 안되어있을 때 로그인 막기
    // public boolean forbiddenLogin(String email) {
    // Optional<User> login = userRepository.findByEmail(email);

    // User user = login.get();

    // if (!user.isActive()) {
    // return false;
    // }

    // return false;
    // }

}
