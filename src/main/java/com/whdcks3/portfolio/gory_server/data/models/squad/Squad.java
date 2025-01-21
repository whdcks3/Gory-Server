package com.whdcks3.portfolio.gory_server.data.models.squad;

import java.lang.annotation.Native;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.enums.Gender;
import com.whdcks3.portfolio.gory_server.enums.JoinType;

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
public class Squad extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String title, category, regionMain, regionSub, description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender genderRequirement;

    @Column(nullable = false)
    private boolean timeSpecified;

    private int maxParticipants, minAge, maxAge;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = true)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinType joinType;

    @Size(max = 1000)
    private String notice;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 1")
    private int currentCount;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @OneToMany(mappedBy = "squad")
    private List<SquadParticipant> participants = new ArrayList<>();

    @Column(nullable = false)
    private boolean closed = false;

    @lombok.Builder
    public Squad(User user, SquadRequest req) {
        this.user = user;
        this.category = req.getCategory();
        this.title = req.getTitle();
        this.description = req.getDescription();
        this.regionMain = req.getRegionMain();
        this.regionSub = req.getRegionSub();
        this.timeSpecified = req.getTimeSpecified();
        if (req.getTimeSpecified().booleanValue()) {
            this.time = req.getTime();
        }
        this.genderRequirement = req.getGenderRequirement();
        this.joinType = req.getJoinType();
        this.maxParticipants = req.getMaxParticipants();
        this.minAge = req.getMinAge();
        this.maxAge = req.getMaxAge();
        this.date = req.getDate();
        this.currentCount = 1;
    }

    public void update(SquadRequest req) {
        this.category = req.getCategory();
        this.title = req.getTitle();
        this.description = req.getDescription();
        this.regionMain = req.getRegionMain();
        this.regionSub = req.getRegionSub();
        this.timeSpecified = req.getTimeSpecified();
        if (req.getTimeSpecified().booleanValue()) {
            this.time = req.getTime();
        }
        this.genderRequirement = req.getGenderRequirement();
        this.joinType = req.getJoinType();
        this.maxParticipants = req.getMaxParticipants();
        this.minAge = req.getMinAge();
        this.maxAge = req.getMaxAge();
        this.date = req.getDate();
        this.currentCount = 1;
    }

    public boolean isJoining(User user) {
        for (SquadParticipant squadParticipant : participants) {
            if (squadParticipant.getUser().getPid() == user.getPid()
                    && squadParticipant.getStatus().equals(SquadParticipant.SquadParticipationStatus.JOINED)) {
                return true;
            }
        }
        return false;
    }

    public void increaseCurrentCount() {
        this.currentCount++;
    }

    public void decreaseCurrentCount() {
        this.currentCount--;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public boolean checkGender(Gender gender) {
        return this.genderRequirement.equals(Gender.ALL) || this.genderRequirement.equals(gender);
    }

}
