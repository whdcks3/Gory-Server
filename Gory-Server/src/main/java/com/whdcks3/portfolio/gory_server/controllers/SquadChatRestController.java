package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.stereotype.Controller;

@Controller
public class SquadChatRestController {

    private final SquadChatService squadChatService;

    public SquadChatController(SquadChatService squadChatService) {
        this.squadChatService = squadChatService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/squad/{squadId}")
    public SquadChatDTO sendMessage(SquadChatDTO chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        squadChatService.saveMessage(chatMessage);
        return chatMessage;
    }
}