package com.whdcks3.portfolio.gory_server.data.models.squad;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.enums.JoinType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class SquadParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "squad_pid")
    private Squad squad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SquadParticipationStatus status;

    public SquadParticipant(User user, Squad squad) {
        this.user = user;
        this.squad = squad;
        this.status = squad.getJoinType().equals(JoinType.DIRECT) ? SquadParticipationStatus.JOINED
                : SquadParticipationStatus.PENDING;
    }

    public enum SquadParticipationStatus {
        JOINED,
        PENDING,
        REJECTED,
        KICKED_OUT
    }
}
