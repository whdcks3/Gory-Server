package com.whdcks3.portfolio.gory_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.chat.Chatroom;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

}
