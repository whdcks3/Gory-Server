package com.whdcks3.portfolio.gory_server.service;

import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.exception.MemberNotEqualsException;
import com.whdcks3.portfolio.gory_server.repositories.SquadParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SquadService {

    private final SquadRepository squadRepository;

    private final SquadParticipantRepository squadParticipantRepository;

    private final UserRepository userRepository;

    public void createSquad(User user, SquadRequest req) {
        Squad squad = new Squad(user, req);
        squad = squadRepository.save(squad);

        SquadParticipant participant = new SquadParticipant(user, squad);
        squadParticipantRepository.save(participant);
    }

    public void modifySquad(Long uid, Long sid, SquadRequest req) {
        User user = userRepository.findById(uid).orElseThrow();
        Squad squad = squadRepository.findById(sid).orElseThrow();
        validateOwner(user, squad);
    }

    public void validateOwner(User user, Squad squad) {
        if (!user.equals(squad.getUser())) {
            throw new MemberNotEqualsException();
        }
    }

}
