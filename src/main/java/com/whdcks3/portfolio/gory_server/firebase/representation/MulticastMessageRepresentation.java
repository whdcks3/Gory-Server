package com.whdcks3.portfolio.gory_server.firebase.representation;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MulticastMessageRepresentation {
    private String data;
    private List<String> registrationTokens;
}
