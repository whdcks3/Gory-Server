package com.whdcks3.data.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
