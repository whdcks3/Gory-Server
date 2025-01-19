// package com.whdcks3.portfolio.gory_server.data.dto;

// import java.time.LocalTime;

// import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;
// import com.whdcks3.portfolio.gory_server.data.models.user.User;

// import lombok.AllArgsConstructor;
// import lombok.Data;

// @Data
// @AllArgsConstructor
// public class SquadChatDto {

// private MessageType type;
// private Long squadChatId;
// private Long sender;
// private String message;
// private LocalTime time;

// public static SquadChatDto toDto(SquadChat squadChat, User user) {
// MessageType type;
// if (squadChat.getType() == MessageType.ENTER) {
// type = MessageType.ENTER;
// } else {
// type = MessageType.TALK;
// }
// Long squadChatId = squadChat.getPid();
// Long sender = user.getPid();
// String message = squadChat.getMessage();
// LocalTime time = LocalTime.now();

// return new SquadChatDto(type, squadChatId, sender, message, time);
// }

// // 입장 메시지, 채팅
// public enum MessageType {
// ENTER, TALK
// }
// }
