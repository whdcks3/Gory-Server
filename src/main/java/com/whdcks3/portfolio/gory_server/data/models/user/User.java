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
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.datetime.DateFormatter;

import com.whdcks3.portfolio.gory_server.common.CommonVO;
import com.whdcks3.portfolio.gory_server.data.models.Role;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.data.requests.UserModifyRequest;
import com.whdcks3.portfolio.gory_server.enums.ERole;

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
public class User extends CommonVO {
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

    @Size(max = 20)
    private String phone;

    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(8) DEFAULT ''")
    private String nickname;

    private LocalDate birth;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean receiveEvent, alarm;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @Size(max = 1)
    private String gender;

    @Column(nullable = false, columnDefinition = "VARCHAR(256) DEFAULT ''")
    private String imageUrl, imagePath, introduction;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_pid"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<UserSquad> squads = new ArrayList<>();

    @Column(nullable = true)
    private String firebase;

    // 계정 활성화용
    private boolean isActive = false;
    private String activationToken;
    private LocalDateTime tokenExpiryDate;

    // 계정 인증메일 기록
    @ElementCollection
    private List<LocalDateTime> loginAttempts = new ArrayList<>();

    // 잠금 해제 시간
    @Column
    private LocalDateTime locked;

    public User(SignupRequest req, String password, Role role, String imageUrl, String imagePath) {
        this.email = req.getEmail();
        this.password = password;
        this.snsType = req.getSnsType();
        this.snsId = req.getSnsId();
        this.roles = new HashSet<>();
        this.roles.add(role);
        this.imageUrl = imageUrl;
        this.imagePath = imagePath;
        this.phone = req.getPhone();
        this.name = req.getName();
        this.birth = LocalDate.parse(req.getBirth(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.gender = req.getGender();
        this.receiveEvent = req.getReceiveEvent().equals("true");
        this.nickname = "";
        this.activationToken = UUID.randomUUID().toString();
        this.tokenExpiryDate = LocalDateTime.now().plusHours(24);
        this.locked = null;
    }

    public void update(UserModifyRequest req, String url, String path) {
        this.introduction = req.getIntroduction();
        System.out.println("update file");
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            deleteImage();
            this.imageUrl = url;
            this.imagePath = path;
        }
    }

    public void deleteImage() {
        if (imagePath.contains("avatar_placeholder")) {
            return;
        }
        new File(imagePath).delete();
    }

    public void setAlarm() {
        alarm = !alarm;
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

    public boolean isLocked() {
        return locked != null && locked.isAfter(LocalDateTime.now());
    }

}
