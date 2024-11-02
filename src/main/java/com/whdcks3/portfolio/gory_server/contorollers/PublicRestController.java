package com.whdcks3.portfolio.gory_server.contorollers;

import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.common.EmailUtils;
import com.whdcks3.portfolio.gory_server.common.Utils;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.data.responses.CommonResponse;
import com.whdcks3.portfolio.gory_server.data.responses.ErrorResponse;
import com.whdcks3.portfolio.gory_server.data.responses.JwtResponse;
import com.whdcks3.portfolio.gory_server.exception.ValidationException;
import com.whdcks3.portfolio.gory_server.repositories.RoleRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.jwt.JwtUtils;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.service.AuthService;

import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class PublicRestController {

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    JavaMailSender mailsender;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtills;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestParam String email,
            @Valid @RequestParam String snsType, @Valid @RequestParam String snsId) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, snsType + snsId));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtills.generateJwtToken(authentication);
        // CustomUserDetails customUserDetails = (CustomUserDetails)
        // authentication.getPrincipal();

        // return ResponseEntity.ok(new JwtResponse(jwt,
        // customUserDetails.getNickname()));
        return ResponseEntity.ok(new JwtResponse(jwt, Utils.getNickname()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {

        if (!authService.signUp(req)) {
            return ResponseEntity.badRequest().body(new ErrorResponse(91, "회원 가입에 오류가 발생했습니다"));
            // return ResponseEntity.badRequest().body("회원 가입에 오류가 발생했습니다");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/find")
    public ResponseEntity<?> findUser(@Valid @RequestParam String email, @RequestParam String code) {
        // TODO: 회원가입형태 찾기 컨트롤러

        return ResponseEntity.ok().build();
    }

    // 회원에게 이메일 보내기
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam String email) {
        authService.sendEmail(email);
        return ResponseEntity.ok().build();
    }

    // 회원 이메일,코드 유효성 인증
    @PostMapping("/validate")
    public ResponseEntity<?> validatingUser(@RequestParam String email, @RequestParam String code) {
        try {
            authService.validateUser(email, code);
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestParam String token) {
        return authService.activateUser(token) ? ResponseEntity.ok("계정이 활성화 되었습니다.") : ResponseEntity.badRequest().body("인증 코드가 잘못되었습니다.");
    }

    @PostMapping("/multiauth")
    public ResponseEntity<?> multiAuthentication(@RequestParam String email) {
        try{
            authService.shouldLock(email);
        } catch (ValidationException e) {
            return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(), e.getMessage()));
        }
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }
}
