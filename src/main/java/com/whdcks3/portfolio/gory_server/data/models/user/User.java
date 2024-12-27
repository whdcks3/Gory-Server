package com.whdcks3.portfolio.gory_server.data.models.user;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.datetime.DateFormatter;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.Role;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.data.requests.UserModifyRequest;
import com.whdcks3.portfolio.gory_server.enums.ERole;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.enums.OAuthProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class User extends BaseEntity {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @NotBlank
    @Size(max = 10)
    private String snsType;

    @Size(max = 120)
    private String snsId;

    // @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    // private OAuthProvider oauthProvider;

    private String carrier;

    @Size(max = 20)
    private String phone;

    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(8) DEFAULT ''")
    private String nickname;

    private LocalDate birth;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean receiveEvent, feedAlarm, feedLikeAlarm;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @Size(max = 1)
    private String gender;

    @Column(nullable = false, columnDefinition = "VARCHAR(256) DEFAULT ''")
    private String imageUrl, introduction;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_pid"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<SquadParticipant> squads = new ArrayList<>();

    @Column(nullable = true)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    private LockType lockType;

    // 계정 인증메일 기록
    @ElementCollection
    @CollectionTable(name = "email_auth_attempts", joinColumns = @JoinColumn(name = "email"))
    private List<LocalDateTime> authAttempts = new ArrayList<>();

    // 잠금 해제 시간
    @Column
    private LocalDateTime lockedUntil;

    public User(SignupRequest req, String password, String imageUrl) {
        this.email = req.getEmail();
        this.password = password;
        this.snsType = req.getSnsType();
        this.snsId = req.getSnsId();
        this.imageUrl = imageUrl;
        this.phone = req.getPhone();
        this.carrier = req.getCarrier();
        this.name = req.getName();
        this.birth = LocalDate.parse(req.getBirth(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.gender = req.getGender();
        this.receiveEvent = req.getReceiveEvent().equals("true");
        this.nickname = "";
        this.lockType = LockType.EMAIL_AUTH;
        this.authAttempts = new ArrayList<>();
    }

    public void update(UserModifyRequest req, String url, String path) {
        this.introduction = req.getIntroduction();
        System.out.println("update file");
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            deleteImage();
            this.imageUrl = url;
        }
    }

    public void deleteImage() {
        if (imageUrl.contains("avatar_placeholder")) {
            return;
        }
        // TODO: delete profile image
        // new File(imageUrl).delete();
    }

    public void setAlarm() {
        feedAlarm = !feedAlarm;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public Boolean equals(User user) {
        if (user != null && user.getPid() == getPid()) {
            return true;
        }
        return false;
    }

    public void tooManyAttempts(LocalDateTime now) {
        lockType = LockType.MANY_ATTEMPTS;
        lockedUntil = now;
    }

    public boolean isWithinLockedTime() {
        return lockType.equals(LockType.MANY_ATTEMPTS) && lockedUntil.isAfter(LocalDateTime.now());
    }

}
