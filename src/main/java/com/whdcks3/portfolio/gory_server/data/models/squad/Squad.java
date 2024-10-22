package com.whdcks3.portfolio.gory_server.data.models.squad;

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
import com.whdcks3.portfolio.gory_server.common.CommonVO;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.models.user.UserSquad;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;

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

    @lombok.Builder
    public Squad(User user, SquadRequest req) {
        this.user = user;
        this.category = req.getCategory();
        this.name = req.getName();
        this.regionMain = req.getRegionMain();
        this.regionSub = req.getRegionSub();
        this.ampm = req.getAmpm();
        this.gender = req.getGender();
        this.introduction = req.getIntroduction();
        this.isTimeSelected = req.getIsTimeSelected();
        this.shouldBeConfirmed = req.getShouldBeConfirmed();
        this.memberCount = req.getMemberCount();
        this.startAge = req.getStartAge();
        this.endAge = req.getEndAge();
        this.hour = req.getHour();
        this.minute = req.getMinute();
        this.date = req.getDate();
        this.currentCount = 1;
    }

    public void update(SquadRequest req) {
        this.category = req.getCategory();
        this.name = req.getName();
        this.regionMain = req.getRegionMain();
        this.regionSub = req.getRegionSub();
        this.ampm = req.getAmpm();
        this.gender = req.getGender();
        this.introduction = req.getIntroduction();
        this.isTimeSelected = req.getIsTimeSelected();
        this.memberCount = req.getMemberCount();
        this.startAge = req.getStartAge();
        this.endAge = req.getEndAge();
        this.hour = req.getHour();
        this.minute = req.getMinute();
        this.date = req.getDate();
    }

    public boolean isJoining(User user) {
        for (UserSquad userSquad : users) {
            if (userSquad.getUser().getPid() == user.getPid() && userSquad.getConfirmed()) {
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

    public boolean checkGender(String gender) {
        return this.gender.equals("누구나") || this.gender.contains(gender);
    }
}
