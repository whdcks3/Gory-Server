package com.whdcks3.portfolio.gory_server.data.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatroomRequest {
    private String category;
    private String title;
    private String content;
    private int maxParticipantCount;

}
