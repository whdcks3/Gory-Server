package com.whdcks3.data.models.user;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.whdcks3.data.models.Chatroom;

public class UserChatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatroom_pid")
    private Chatroom chatroom;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean confirmed, denied, banned;
}
