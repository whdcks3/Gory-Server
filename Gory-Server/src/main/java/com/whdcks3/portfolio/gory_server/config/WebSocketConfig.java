package com.whdcks3.portfolio.gory_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    // WebSocketHandler에 관한 생성자 추가
    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        // /api/squadChat으로 요청이 들어오면 websocket 통신을 진행한다.
        registry.addHandler(webSocketHandler, "/api/squadChat").setAllowedOrigins("*");
    }
}
