package com.whdcks3.data.models.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import com.whdcks3.common.CommonVO;
import com.whdcks3.enums.ERole;

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
    private String ninkname;

    private LocalDate birth;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @Size(max = 1)
    private String gender;

    @Column(nullable = false, columnDefinition = "VARCHAR(256) DEFAULT ''")
    private String imageUrl, imagePath, introduction;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_pid"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<ERole> roles;

    @OneToMany(mappedBy = "user")
    private List<UserSquad> squads = new ArrayList<>();

    @Column(nullable = true)
    private String firebase;

}
