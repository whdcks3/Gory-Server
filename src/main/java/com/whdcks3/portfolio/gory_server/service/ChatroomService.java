package com.whdcks3.portfolio.gory_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.models.chat.Chatroom;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.ChatroomRequest;
import com.whdcks3.portfolio.gory_server.repositories.ChatroomRepository;

@Service
public class ChatroomService {

    @Autowired
    ChatroomRepository chatroomRepository;

    public void createChatroom(User user, ChatroomRequest req) {
        Chatroom chat = new Chatroom(user, req);
        chatroomRepository.save(chat);
    }

}
