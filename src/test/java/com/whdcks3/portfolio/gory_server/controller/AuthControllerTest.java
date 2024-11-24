package com.whdcks3.portfolio.gory_server.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.whdcks3.portfolio.gory_server.data.models.RandomCode;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.RandomCodeRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.service.AuthService;
import com.whdcks3.portfolio.gory_server.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RandomCodeRepository randomCodeRepository;

    @Autowired
    private UserRepository userRepository;

    private String token, activateToken;

    private String nickname;

    private RandomCode code;

    @BeforeEach
    void setup() {
        // userRepository.deleteAll();
        User user = userRepository.findByEmail("sj012944@gmail.com").get();
        activateToken = user.getActivationToken();
        nickname = user.getNickname();
        code = randomCodeRepository.findByCode("sj012944@gmail.com");
        // authService.signUp(
        // new SignupRequest("sj012944@gmail.com", "kakao", "00000", "홍길동",
        // "01012341234", "M", "19970101", "true",
        // "", ""));
    }

    @Test
    void testSignupNonExists() throws Exception {
        // {"email":"sj012944@gmail.com","snsType":"kakao","snsId":"00000","name":"홍길동","phone":"01012341234","phoneType":"SKT","birth":"19900101","gender":"M","receiveEvent":"true"}
        mockMvc.perform(post("/api/auth/signup")
                // .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"email\":\"sj012944@gmail.com\",\"snsType\":\"kakao\",\"snsId\":\"00000\",\"name\":\"홍길동\",\"phone\":\"01012341234\",\"phoneType\":\"SKT\",\"birth\":\"19900101\",\"gender\":\"M\",\"receiveEvent\":\"true\"}"))
                .andExpect(status().isOk());
        // .andExpect(jsonPath("$.nickname").exists());
    }

    @Test
    void testActivate() throws Exception {
        mockMvc.perform(get("/api/auth/activate?token=" + activateToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSend() throws Exception {
        mockMvc.perform(post("/api/auth/send")
                .param("email", "sj012944@gamil.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testValidate() throws Exception {
        mockMvc.perform(post("/api/auth/validate")
                .param("email", "sj012944@gmail.com")
                .param("code", "799934"))
                .andExpect(status().isOk());
    }

    @Test
    void testMultiauth() throws Exception {
        mockMvc.perform(post("/api/auth/multiauth")
                .param("email", "sj012944@gmail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserName() throws Exception {
        mockMvc.perform(get("/api/user/generate_username"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateNickName() throws Exception {
        mockMvc.perform(post("/api/user/update_nickname")
                .param("nickname", "호감가는 새나"))
                .andExpect(status().isOk());
    }

    @AfterEach
    void end() {
        // userRepository.findAll().get(0).getRoles().clear();
        // userRepository.deleteAll();
    }

}
