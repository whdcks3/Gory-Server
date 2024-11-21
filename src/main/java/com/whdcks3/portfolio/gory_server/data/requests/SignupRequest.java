package com.whdcks3.portfolio.gory_server.data.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Size(max = 50)
    @Email
    private String email;

    private String snsType;
    private String snsId;
    private String name;
    private String phone;
    private String gender;
    private String birth;
    private String receiveEvent;

    private String txSeqNo;
    private String optNo;
}
