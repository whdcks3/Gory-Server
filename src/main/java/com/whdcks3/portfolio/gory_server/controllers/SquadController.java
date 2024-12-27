package com.whdcks3.portfolio.gory_server.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.rpc.context.AttributeContext.Response;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.data.responses.CommonResponse;
import com.whdcks3.portfolio.gory_server.service.SquadService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/squad")
public class SquadController {
    @Autowired
    SquadService squadService;

    @PostMapping()
    public ResponseEntity<?> createSquad(@AuthenticationPrincipal User user, @Valid @RequestBody SquadRequest req) {
        squadService.createSquad(user, req);
        return ResponseEntity.ok().body(new CommonResponse(100, "성공"));

    }

}
