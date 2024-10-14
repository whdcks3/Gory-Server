package com.whdcks3.contorollers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.common.Utils;
import com.whdcks3.data.requests.SignupRequest;
import com.whdcks3.data.responses.ErrorResponse;
import com.whdcks3.data.responses.JwtResponse;
import com.whdcks3.repositories.RoleRepository;
import com.whdcks3.repositories.UserRepository;
import com.whdcks3.security.jwt.JwtUtils;
import com.whdcks3.security.service.CustomUserDetails;
import com.whdcks3.service.AuthService;

import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class PublicController {

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
    public ResponseEntity<?> findUser(@RequestParam String email, @RequestParam String code) {
        // TODO: 회원가입형태 찾기 컨트롤러

        return ResponseEntity.ok().build();
    }

}
