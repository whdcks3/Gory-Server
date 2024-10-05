package com.whdcks3.data.models.squad;

import java.time.LocalDate;
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
import com.whdcks3.data.models.user.UserSquad;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "squad")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
public class Squad extends CommonVO {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String name, category, regionMain, regionSub, ampm, gender, introduction;
    private Boolean isTimeSelected, shouldBeConfirmed;
    private Integer memberCount, startAge, endAge, hour, minute;
    private LocalDate date;

    @Size(max = 1000)
    private String notice;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 1")
    private int currentCount;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @OneToMany(mappedBy = "squad")
    private List<UserSquad> users = new ArrayList<>();

}
