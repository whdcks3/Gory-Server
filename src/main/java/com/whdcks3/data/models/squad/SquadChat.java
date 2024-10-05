package com.whdcks3.data.models.squad;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.common.CommonVO;
import com.whdcks3.data.models.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "squad_chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
public class SquadChat extends CommonVO {

    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "suqad_pid")
    private Squad squad;

    @Size(max = 1000)
    private String message;

    private int type = 0;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int imageCount;

    @OneToMany(mappedBy = "squadChat", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<SquadChatImage> images;

    @Column(nullable = false, columnDefinition = "BOOLEAN DAFAUT true")
    private boolean deletable;
}
