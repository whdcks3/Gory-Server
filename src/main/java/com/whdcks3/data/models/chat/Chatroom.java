package com.whdcks3.data.models.chat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.common.CommonVO;
import com.whdcks3.data.models.user.User;
import com.whdcks3.data.models.user.UserChatroom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chatroom")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
public class Chatroom extends CommonVO {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String category;

    private String title;

    @Size(max = 1000)
    private String notice;

    private Integer memberCount;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 100)
    private String content;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 1")
    private int currentCount;

    @OneToMany(mappedBy = "chatroom")
    private List<UserChatroom> users = new ArrayList<>();

}
