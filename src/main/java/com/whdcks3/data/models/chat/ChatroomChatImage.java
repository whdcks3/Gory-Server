package com.whdcks3.data.models.chat;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class ChatroomChatImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String uniqueName;

    @Column(nullable = false)
    private String originName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_chat_pid", nullable = false)
    private ChatroomChat chatroomChat;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate createDate;

}
