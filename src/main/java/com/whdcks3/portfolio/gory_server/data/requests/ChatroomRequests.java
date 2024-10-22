package com.whdcks3.portfolio.gory_server.data.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomRequests {
    private Long pid;
    private String title, content, category;
    private Integer memberCount;
}
