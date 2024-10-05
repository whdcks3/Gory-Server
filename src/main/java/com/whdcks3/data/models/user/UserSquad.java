package com.whdcks3.data.models.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.whdcks3.data.models.squad.Squad;

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
public class UserSquad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "squad_pid")
    private Squad squad;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean confirmed, denied, banned;
}
