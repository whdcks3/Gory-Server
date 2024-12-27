package com.whdcks3.portfolio.gory_server.service;

import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.repositories.SquadParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SquadService {

    private final SquadRepository squadRepository;

    private final SquadParticipantRepository squadParticipantRepository;

    public void createSquad(User user, SquadRequest req) {
        Squad squad = new Squad(user, req);
        squad = squadRepository.save(squad);

        SquadParticipant participant = new SquadParticipant(user, squad);
        squadParticipantRepository.save(participant);
    }
}
