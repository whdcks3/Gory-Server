package com.whdcks3.security.service;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long pid;
    private String email;
    private String nickname;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String snsType;
    @JsonIgnore
    private String snsId;
    @JsonIgnore
    private String phone;
    @JsonIgnore
    private String name;
    @JsonIgnore
    private LocalDate birth;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String email, String nickname, String password, String phone, String snsType,
            String snsId,
            String name, LocalDate birth, Collection<? extends GrantedAuthority> authorities) {
        this.pid = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.snsType = snsType;
        this.snsId = snsId;
        this.name = name;
        this.birth = birth;
        this.authorities = authorities;

    }

    public static CustomUserDetails build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name))
                .collect(Collectors.toList());

        return new CustomUserDetails(
                user.getPid(),
                user.getEmail(),
        )
    }

}
